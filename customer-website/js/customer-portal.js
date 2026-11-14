/*
 * QA Clothing Factory - Customer Portal
 *
 * Supports:
 * 1. Quotation tracking
 * 2. Order-related tracking
 * 3. Quotation acceptance and rejection
 *
 * Existing API endpoints:
 * POST /api/CustomerQuotations/track
 * POST /api/CustomerQuotations/decision
 *
 * Customers authenticate using their quotation
 * reference and private 12-digit tracking PIN.
 */

(function () {
    'use strict';

    const API_BASE_URL =
        'https://sa-stellar-solution-mobile-app.onrender.com';

    const TRACK_ENDPOINT =
        '/api/CustomerQuotations/track';

    const DECISION_ENDPOINT =
        '/api/CustomerQuotations/decision';

    const referencePattern =
        /^QA-\d{8}-[A-Z0-9]{12}$/i;

    const trackingPinPattern = /^\d{12}$/;

    const urlParams = new URLSearchParams(
        window.location.search
    );

    const referenceFromUrl =
        urlParams.get('reference') || '';

    let verifiedQuotation = null;
    let verifiedCredentials = null;
    let requestInProgress = false;

    // ==========================================
    // GENERAL HELPERS
    // ==========================================

    function byId(id) {
        return document.getElementById(id);
    }

    function getField(form, name) {
        const field = form.elements.namedItem(name);

        return field
            ? String(field.value || '').trim()
            : '';
    }

    function showMessage(element, message, isError) {
        if (!element) return;

        element.hidden = false;
        element.textContent = message;

        element.classList.toggle(
            'error',
            Boolean(isError)
        );
    }

    function hideMessage(element) {
        if (!element) return;

        element.hidden = true;
        element.textContent = '';
        element.classList.remove('error');
    }

    function setBusy(button, busy, loadingText) {
        if (!button) return;

        if (busy) {
            button.dataset.originalText =
                button.textContent;

            button.disabled = true;
            button.textContent = loadingText;
        } else {
            button.disabled = false;

            if (button.dataset.originalText) {
                button.textContent =
                    button.dataset.originalText;

                delete button.dataset.originalText;
            }
        }
    }

    function clearPin(form) {
        const pinField =
            form.elements.namedItem('trackingPin');

        if (pinField) {
            pinField.value = '';
        }
    }

    function validateCredentials(reference, pin) {
        if (!referencePattern.test(reference)) {
            return (
                'Enter a valid quotation reference ' +
                'in the format QA-YYYYMMDD-XXXXXXXXXXXX.'
            );
        }

        if (!trackingPinPattern.test(pin)) {
            return 'Enter your valid 12-digit tracking PIN.';
        }

        return '';
    }

    function getApiError(data, statusCode) {
        if (statusCode === 429) {
            return (
                'Too many verification attempts. ' +
                'Please wait before trying again.'
            );
        }

        if (
            statusCode === 401 ||
            statusCode === 403 ||
            statusCode === 404
        ) {
            return (
                'The quotation reference and tracking PIN ' +
                'could not be verified. Check both details.'
            );
        }

        if (
            data &&
            typeof data.message === 'string'
        ) {
            return data.message;
        }

        if (
            data &&
            typeof data.title === 'string'
        ) {
            return data.title;
        }

        return (
            'The server could not complete your request. ' +
            'Please try again.'
        );
    }

    async function postJson(endpoint, payload) {
        let response;

        try {
            response = await fetch(
                API_BASE_URL + endpoint,
                {
                    method: 'POST',
                    mode: 'cors',
                    cache: 'no-store',
                    credentials: 'omit',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify(payload)
                }
            );
        } catch (_) {
            throw new Error(
                'Unable to connect to the server. ' +
                'Check your internet connection and try again.'
            );
        }

        let data = null;

        try {
            data = await response.json();
        } catch (_) {
            // Some server errors have no JSON body.
        }

        if (!response.ok) {
            throw new Error(
                getApiError(data, response.status)
            );
        }

        if (!data || data.success !== true) {
            throw new Error(
                'The server returned an unexpected response.'
            );
        }

        return data;
    }

    function formatCurrency(value) {
        if (
            value === null ||
            value === undefined ||
            value === ''
        ) {
            return 'Not yet quoted';
        }

        const amount = Number(value);

        if (!Number.isFinite(amount)) {
            return 'Not available';
        }

        return new Intl.NumberFormat('en-ZA', {
            style: 'currency',
            currency: 'ZAR'
        }).format(amount);
    }

    function formatDate(value) {
        if (!value) {
            return 'Not available';
        }

        const date = new Date(value);

        if (Number.isNaN(date.getTime())) {
            return String(value);
        }

        return date.toLocaleString('en-ZA', {
            dateStyle: 'medium',
            timeStyle: 'short'
        });
    }

    function formatDeadline(value) {
        if (!value) {
            return 'Not available';
        }

        const datePart = String(value).slice(0, 10);

        if (/^\d{4}-\d{2}-\d{2}$/.test(datePart)) {
            const parts = datePart.split('-');

            return (
                parts[2] + '/' +
                parts[1] + '/' +
                parts[0]
            );
        }

        return formatDate(value);
    }

    function normalizeDecision(value) {
        return String(value || 'Pending').trim();
    }

    function canDecide(quotation) {
        if (!quotation) return false;

        return (
            String(
                quotation.status || ''
            ).toLowerCase() === 'quoted' &&

            Number(quotation.quotedAmount) > 0 &&

            normalizeDecision(
                quotation.customerDecision
            ).toLowerCase() === 'pending'
        );
    }

    function addDetail(container, label, value) {
        if (!container) return;

        const term = document.createElement('dt');
        term.textContent = label;

        const description =
            document.createElement('dd');

        description.textContent =
            value === null || value === undefined
                ? 'Not available'
                : String(value);

        container.appendChild(term);
        container.appendChild(description);
    }

    function renderQuotationDetails(container, quotation) {
        if (!container) return;

        container.replaceChildren();

        addDetail(
            container,
            'Quotation Reference',
            quotation.referenceNumber
        );

        addDetail(
            container,
            'Product Category',
            quotation.category
        );

        addDetail(
            container,
            'Estimated Quantity',
            quotation.quantity
        );

        addDetail(
            container,
            'Requested Deadline',
            formatDeadline(quotation.deadline)
        );

        addDetail(
            container,
            'Quotation Status',
            quotation.status
        );

        addDetail(
            container,
            'Quoted Amount',
            formatCurrency(quotation.quotedAmount)
        );

        addDetail(
            container,
            'Customer Decision',
            normalizeDecision(
                quotation.customerDecision
            )
        );

        addDetail(
            container,
            'Date Submitted',
            formatDate(quotation.createdAtUtc)
        );

        if (quotation.customerDecisionAtUtc) {
            addDetail(
                container,
                'Decision Recorded',
                formatDate(
                    quotation.customerDecisionAtUtc
                )
            );
        }
    }

    function renderTimeline(list, history) {
        if (!list) return;

        list.replaceChildren();

        if (
            !Array.isArray(history) ||
            history.length === 0
        ) {
            const item = document.createElement('li');

            item.textContent =
                'No history updates are available yet.';

            list.appendChild(item);
            return;
        }

        history.forEach(function (entry) {
            const item = document.createElement('li');

            const status = document.createElement('strong');
            status.textContent =
                String(entry.status || 'Update');

            const description =
                document.createElement('p');

            description.textContent =
                String(entry.description || '');

            const date = document.createElement('small');
            date.textContent = formatDate(
                entry.createdAtUtc
            );

            item.appendChild(status);
            item.appendChild(description);
            item.appendChild(date);

            list.appendChild(item);
        });
    }

    function prefillReference() {
        if (!referencePattern.test(referenceFromUrl)) {
            return;
        }

        const reference =
            referenceFromUrl.toUpperCase();

        [
            'reference',
            'decisionReference',
            'orderReference'
        ].forEach(function (id) {
            const field = byId(id);

            if (field) {
                field.value = reference;
            }
        });
    }

    function watchInputs(form, callback) {
        const referenceField =
            form.elements.namedItem('reference');

        const pinField =
            form.elements.namedItem('trackingPin');

        [referenceField, pinField].forEach(
            function (field) {
                if (!field) return;

                field.addEventListener(
                    'input',
                    callback
                );
            }
        );
    }

    // ==========================================
    // QUOTATION TRACKING
    // track-quotation.html
    // ==========================================

    function resetTrackingResults() {
        const result = byId('quoteResult');
        const actions = byId('quoteActions');
        const history = byId('historySection');

        if (result) result.hidden = true;
        if (actions) actions.hidden = true;
        if (history) history.hidden = true;

        const details = byId('quoteDetails');
        const historyList = byId('historyList');

        if (details) details.replaceChildren();
        if (historyList) historyList.replaceChildren();
    }

    function renderTrackingResult(data) {
        if (!data.quotation) {
            throw new Error(
                'Quotation details were missing from ' +
                'the server response.'
            );
        }

        const quotation = data.quotation;

        renderQuotationDetails(
            byId('quoteDetails'),
            quotation
        );

        const badge = byId('quoteBadge');

        if (badge) {
            badge.textContent =
                String(quotation.status || 'Unknown');
        }

        const actions = byId('quoteActions');
        const decisionLink = byId('decisionLink');

        if (actions) {
            actions.hidden = !canDecide(quotation);
        }

        if (decisionLink) {
            const url = new URL(
                'quotation-decision.html',
                window.location.href
            );

            url.searchParams.set(
                'reference',
                quotation.referenceNumber
            );

            // Never include the PIN in the URL.
            decisionLink.href = url.toString();
        }

        const result = byId('quoteResult');

        if (result) result.hidden = false;

        const historyList = byId('historyList');
        const historySection = byId('historySection');

        if (historyList) {
            renderTimeline(
                historyList,
                data.history
            );
        }

        if (historySection) {
            historySection.hidden = false;
        }
    }

    function initializeTrackingPage() {
        const form = byId('portalLookup');

        if (!form) return;

        const message = byId('lookupMessage');

        const button = byId('trackButton') ||
            form.querySelector('button[type="submit"]');

        watchInputs(form, function () {
            if (requestInProgress) return;

            resetTrackingResults();
            hideMessage(message);
        });

        form.addEventListener(
            'submit',
            async function (event) {
                event.preventDefault();

                if (requestInProgress) return;

                resetTrackingResults();
                hideMessage(message);

                const reference = getField(
                    form,
                    'reference'
                ).toUpperCase();

                const trackingPin = getField(
                    form,
                    'trackingPin'
                );

                const error = validateCredentials(
                    reference,
                    trackingPin
                );

                if (error) {
                    showMessage(message, error, true);
                    return;
                }

                requestInProgress = true;

                setBusy(
                    button,
                    true,
                    'Verifying quotation...'
                );

                try {
                    const data = await postJson(
                        TRACK_ENDPOINT,
                        {
                            referenceNumber: reference,
                            trackingPin: trackingPin
                        }
                    );

                    renderTrackingResult(data);

                    showMessage(
                        message,
                        'Quotation verified successfully.',
                        false
                    );
                } catch (error) {
                    resetTrackingResults();

                    showMessage(
                        message,
                        error.message ||
                            'Unable to retrieve quotation.',
                        true
                    );
                } finally {
                    requestInProgress = false;
                    setBusy(button, false);
                    clearPin(form);
                }
            }
        );
    }

    // ==========================================
    // ORDER-RELATED TRACKING
    // track-order.html
    // ==========================================

    function resetOrderResults() {
        const result = byId('orderResult');
        const details = byId('orderDetails');
        const timeline = byId('orderTimeline');
        const badge = byId('orderBadge');

        if (result) result.hidden = true;
        if (details) details.replaceChildren();
        if (timeline) timeline.replaceChildren();
        if (badge) badge.textContent = '';
    }

    function renderOrderResult(data) {
        if (!data.quotation) {
            throw new Error(
                'Quotation details were missing from ' +
                'the server response.'
            );
        }

        const quotation = data.quotation;

        renderQuotationDetails(
            byId('orderDetails'),
            quotation
        );

        const badge = byId('orderBadge');

        if (badge) {
            const decision = normalizeDecision(
                quotation.customerDecision
            );

            badge.textContent =
                decision.toLowerCase() === 'accepted'
                    ? 'Quotation Accepted'
                    : String(
                        quotation.status || 'Pending'
                    );
        }

        renderTimeline(
            byId('orderTimeline'),
            data.history
        );

        const result = byId('orderResult');

        if (result) {
            result.hidden = false;
        }
    }

    function initializeOrderPage() {
        const form = byId('orderLookup');

        if (!form) return;

        const message = byId('orderLookupMessage');

        const button = byId('orderTrackButton') ||
            form.querySelector('button[type="submit"]');

        watchInputs(form, function () {
            if (requestInProgress) return;

            resetOrderResults();
            hideMessage(message);
        });

        form.addEventListener(
            'submit',
            async function (event) {
                event.preventDefault();

                if (requestInProgress) return;

                resetOrderResults();
                hideMessage(message);

                const reference = getField(
                    form,
                    'reference'
                ).toUpperCase();

                const trackingPin = getField(
                    form,
                    'trackingPin'
                );

                const error = validateCredentials(
                    reference,
                    trackingPin
                );

                if (error) {
                    showMessage(message, error, true);
                    return;
                }

                requestInProgress = true;

                setBusy(
                    button,
                    true,
                    'Checking order updates...'
                );

                try {
                    const data = await postJson(
                        TRACK_ENDPOINT,
                        {
                            referenceNumber: reference,
                            trackingPin: trackingPin
                        }
                    );

                    renderOrderResult(data);

                    showMessage(
                        message,
                        'Your available quotation and ' +
                        'order-related updates have been retrieved.',
                        false
                    );
                } catch (error) {
                    resetOrderResults();

                    showMessage(
                        message,
                        error.message ||
                            'Unable to retrieve updates.',
                        true
                    );
                } finally {
                    requestInProgress = false;
                    setBusy(button, false);
                    clearPin(form);
                }
            }
        );
    }

    // ==========================================
    // QUOTATION ACCEPTANCE / REJECTION
    // quotation-decision.html
    // ==========================================

    function resetDecisionResults() {
        verifiedQuotation = null;
        verifiedCredentials = null;

        const verified = byId('decisionVerified');
        const details = byId('decisionDetails');

        const accept = byId('acceptQuote');
        const decline = byId('declineQuote');

        if (verified) verified.hidden = true;
        if (details) details.replaceChildren();

        if (accept) accept.disabled = true;
        if (decline) decline.disabled = true;
    }

    function renderDecisionDetails(quotation) {
        renderQuotationDetails(
            byId('decisionDetails'),
            quotation
        );

        const verified = byId('decisionVerified');

        if (verified) {
            verified.hidden = false;
        }

        const eligible = canDecide(quotation);

        const accept = byId('acceptQuote');
        const decline = byId('declineQuote');

        if (accept) accept.disabled = !eligible;
        if (decline) decline.disabled = !eligible;

        if (!eligible) {
            const message = byId('decisionMessage');

            if (
                normalizeDecision(
                    quotation.customerDecision
                ).toLowerCase() !== 'pending'
            ) {
                showMessage(
                    message,
                    'A decision has already been recorded ' +
                    'for this quotation.',
                    false
                );
            } else {
                showMessage(
                    message,
                    'This quotation is not currently ' +
                    'available for acceptance or rejection.',
                    false
                );
            }
        }
    }

    function initializeDecisionPage() {
        const form = byId('decisionForm');

        if (!form) return;

        const message = byId('decisionMessage');

        const verifyButton =
            form.querySelector('button[type="submit"]');

        const acceptButton = byId('acceptQuote');
        const declineButton = byId('declineQuote');

        watchInputs(form, function () {
            if (requestInProgress) return;

            resetDecisionResults();
            hideMessage(message);
        });

        form.addEventListener(
            'submit',
            async function (event) {
                event.preventDefault();

                if (requestInProgress) return;

                resetDecisionResults();
                hideMessage(message);

                const reference = getField(
                    form,
                    'reference'
                ).toUpperCase();

                const trackingPin = getField(
                    form,
                    'trackingPin'
                );

                const error = validateCredentials(
                    reference,
                    trackingPin
                );

                if (error) {
                    showMessage(message, error, true);
                    return;
                }

                requestInProgress = true;

                setBusy(
                    verifyButton,
                    true,
                    'Verifying quotation...'
                );

                try {
                    const data = await postJson(
                        TRACK_ENDPOINT,
                        {
                            referenceNumber: reference,
                            trackingPin: trackingPin
                        }
                    );

                    if (!data.quotation) {
                        throw new Error(
                            'Quotation details were not returned.'
                        );
                    }

                    verifiedQuotation = data.quotation;

                    verifiedCredentials = {
                        referenceNumber: reference,
                        trackingPin: trackingPin
                    };

                    renderDecisionDetails(
                        verifiedQuotation
                    );

                    if (canDecide(verifiedQuotation)) {
                        showMessage(
                            message,
                            'Quotation verified. Review the ' +
                            'amount before accepting or rejecting.',
                            false
                        );
                    }
                } catch (error) {
                    resetDecisionResults();

                    showMessage(
                        message,
                        error.message ||
                            'Verification failed.',
                        true
                    );
                } finally {
                    requestInProgress = false;
                    setBusy(verifyButton, false);
                    clearPin(form);
                }
            }
        );

        async function submitDecision(decision) {
            if (
                requestInProgress ||
                !verifiedQuotation ||
                !verifiedCredentials ||
                !canDecide(verifiedQuotation)
            ) {
                return;
            }

            const action =
                decision === 'Accepted'
                    ? 'accept'
                    : 'reject';

            const confirmed = window.confirm(
                'Are you sure you want to ' +
                action +
                ' this quotation for ' +
                formatCurrency(
                    verifiedQuotation.quotedAmount
                ) +
                '? This decision cannot be changed here.'
            );

            if (!confirmed) return;

            requestInProgress = true;

            if (acceptButton) {
                acceptButton.disabled = true;
            }

            if (declineButton) {
                declineButton.disabled = true;
            }

            showMessage(
                message,
                'Submitting your decision...',
                false
            );

            try {
                const data = await postJson(
                    DECISION_ENDPOINT,
                    {
                        referenceNumber:
                            verifiedCredentials.referenceNumber,

                        trackingPin:
                            verifiedCredentials.trackingPin,

                        decision: decision
                    }
                );

                verifiedQuotation.customerDecision =
                    data.customerDecision || decision;

                verifiedQuotation.customerDecisionAtUtc =
                    data.customerDecisionAtUtc || null;

                renderDecisionDetails(
                    verifiedQuotation
                );

                showMessage(
                    message,
                    'Your quotation has been ' +
                    String(
                        verifiedQuotation.customerDecision
                    ).toLowerCase() +
                    ' successfully. QA Clothing Factory ' +
                    'has recorded your decision.',
                    false
                );

                verifiedCredentials = null;

            } catch (error) {
                verifiedCredentials = null;

                if (acceptButton) {
                    acceptButton.disabled = true;
                }

                if (declineButton) {
                    declineButton.disabled = true;
                }

                showMessage(
                    message,
                    (
                        error.message ||
                        'Unable to submit your decision.'
                    ) +
                    ' Please verify the quotation again ' +
                    'before attempting another decision.',
                    true
                );
            } finally {
                requestInProgress = false;
            }
        }

        if (acceptButton) {
            acceptButton.addEventListener(
                'click',
                function () {
                    submitDecision('Accepted');
                }
            );
        }

        if (declineButton) {
            declineButton.addEventListener(
                'click',
                function () {
                    submitDecision('Rejected');
                }
            );
        }
    }

    // ==========================================
    // INITIALIZE THE CURRENT PAGE
    // ==========================================

    prefillReference();

    initializeTrackingPage();
    initializeOrderPage();
    initializeDecisionPage();

})();
