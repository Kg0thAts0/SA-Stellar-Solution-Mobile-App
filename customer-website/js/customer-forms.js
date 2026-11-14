
/*
 * QA Clothing Factory - Customer Forms
 * Quotation submission and general contact form.
 * Customer tracking PIN is displayed only in the
 * successful submission response.
 */

(function () {
    'use strict';

    const API_BASE_URL =
        'https://sa-stellar-solution-mobile-app.onrender.com';

    document.addEventListener('DOMContentLoaded', function () {
        initializeQuotationForm();
        initializeContactForm();
    });

    // ==========================================
    // HELPER FUNCTIONS
    // ==========================================

    function getField(form, name) {
        const field = form.elements.namedItem(name);

        return field
            ? String(field.value || '').trim()
            : '';
    }

    function getLocalToday() {
        const now = new Date();

        return new Date(
            now.getTime() -
            now.getTimezoneOffset() * 60000
        ).toISOString().slice(0, 10);
    }

    function getMessageElement(form) {
        let element = form.querySelector(
            '.form-api-message'
        );

        if (!element) {
            element = document.createElement('div');

            element.className = 'form-api-message';
            element.setAttribute('role', 'status');
            element.setAttribute('aria-live', 'polite');

            element.style.padding = '14px';
            element.style.marginTop = '16px';
            element.style.borderRadius = '8px';
            element.style.fontSize = '14px';
            element.style.lineHeight = '1.6';

            form.appendChild(element);
        }

        return element;
    }

    function displayMessage(form, message, isSuccess) {
        const element = getMessageElement(form);

        element.replaceChildren();

        element.style.backgroundColor = isSuccess
            ? '#e8f5e9'
            : '#ffebee';

        element.style.color = isSuccess
            ? '#1b5e20'
            : '#b71c1c';

        element.style.border = isSuccess
            ? '1px solid #a5d6a7'
            : '1px solid #ef9a9a';

        element.textContent = message;

        return element;
    }

    function clearMessage(form) {
        form.querySelector('.form-api-message')?.remove();
    }

    function setSubmitting(form, submitting) {
        const button = form.querySelector(
            'button[type="submit"]'
        );

        if (!button) return;

        if (submitting) {
            button.dataset.originalHtml = button.innerHTML;

            button.disabled = true;
            button.style.opacity = '0.65';
            button.style.cursor = 'wait';
            button.textContent = 'Submitting...';

        } else {
            button.disabled = false;
            button.style.opacity = '';
            button.style.cursor = '';

            if (button.dataset.originalHtml) {
                button.innerHTML =
                    button.dataset.originalHtml;

                delete button.dataset.originalHtml;
            }
        }
    }

    function isHoneypotFilled(form) {
        return Boolean(getField(form, 'website'));
    }

    function getApiError(data, statusCode) {
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

        if (data && data.errors) {
            const messages = Object.values(data.errors)
                .flat()
                .map(String);

            if (messages.length) {
                return messages.join(' ');
            }
        }

        return (
            'The server could not process your request. ' +
            'HTTP status: ' + statusCode
        );
    }

    function appendText(parent, tag, text) {
        const element = document.createElement(tag);

        element.textContent = text;
        parent.appendChild(element);

        return element;
    }

    // ==========================================
    // QUOTATION CONFIRMATION
    // ==========================================

    function displayQuotationConfirmation(form, data) {
        const referenceNumber = String(
            data.referenceNumber || ''
        ).trim();

        const trackingPin = String(
            data.trackingPin || ''
        ).trim();

        const message = displayMessage(
            form,
            'Your quotation request was submitted successfully!',
            true
        );

        if (referenceNumber) {
            const reference = appendText(
                message,
                'p',
                'Quotation reference: ' + referenceNumber
            );

            reference.style.fontWeight = '600';
        }

        if (/^\d{12}$/.test(trackingPin)) {
            const label = appendText(
                message,
                'p',
                'Your private 12-digit tracking PIN:'
            );

            label.style.marginBottom = '4px';

            const pin = appendText(
                message,
                'p',
                trackingPin
            );

            pin.style.fontSize = '22px';
            pin.style.fontWeight = '700';
            pin.style.letterSpacing = '2px';
            pin.style.overflowWrap = 'anywhere';

            appendText(
                message,
                'p',
                'IMPORTANT: Save your reference and PIN ' +
                'securely now. You will need both to track ' +
                'your quotation and accept or reject an offer. ' +
                'The PIN will not be shown again after you ' +
                'leave or refresh this page.'
            );

            // Copy quotation reference and PIN.
            const copyButton =
                document.createElement('button');

            copyButton.type = 'button';
            copyButton.className = 'portal-outline';
            copyButton.textContent =
                'Copy reference and PIN';

            copyButton.style.marginRight = '12px';

            copyButton.addEventListener(
                'click',
                async function () {
                    const details =
                        'Quotation reference: ' +
                        referenceNumber +
                        '\nTracking PIN: ' +
                        trackingPin;

                    try {
                        if (
                            !navigator.clipboard ||
                            !navigator.clipboard.writeText
                        ) {
                            throw new Error(
                                'Clipboard API unavailable'
                            );
                        }

                        await navigator.clipboard.writeText(
                            details
                        );

                        copyButton.textContent = 'Copied!';

                    } catch (_) {
                        copyButton.textContent =
                            'Copy unavailable — ' +
                            'save the details above';
                    }
                }
            );

            message.appendChild(copyButton);

        } else {
            appendText(
                message,
                'p',
                'A tracking PIN was not included in the ' +
                'server response. Keep your reference and ' +
                'contact the factory for assistance. ' +
                'Do not submit the same quotation again ' +
                'just to obtain a PIN.'
            );
        }

        if (referenceNumber) {
            const link = document.createElement('a');

            link.href =
                'track-quotation.html?reference=' +
                encodeURIComponent(referenceNumber);

            link.textContent = 'Track your quotation';
            link.className = 'portal-followup-link';

            message.appendChild(link);
        }
    }

    // ==========================================
    // QUOTATION REQUEST FORM
    // ==========================================

    function initializeQuotationForm() {
        const form = document.getElementById('quoteForm');

        if (!form) return;

        const deadlineInput =
            document.getElementById('quote_deadline');

        if (deadlineInput) {
            deadlineInput.min = getLocalToday();
        }

        let submitting = false;

        form.addEventListener(
            'submit',
            async function (event) {
                event.preventDefault();

                if (submitting) return;

                clearMessage(form);

                // Preserve honeypot validation.
                if (isHoneypotFilled(form)) {
                    displayMessage(
                        form,
                        'Your request could not be submitted.',
                        false
                    );

                    return;
                }

                if (!form.checkValidity()) {
                    form.reportValidity();
                    return;
                }

                if (
                    typeof window.validateContactForm ===
                    'function' &&
                    !window.validateContactForm('quoteForm')
                ) {
                    displayMessage(
                        form,
                        'Please correct the highlighted fields.',
                        false
                    );

                    return;
                }

                const quantity = Number(
                    getField(form, 'quantity')
                );

                const deadline = getField(
                    form,
                    'deadline'
                );

                const specifications = getField(
                    form,
                    'specifications'
                );

                // Validate quantity.
                if (
                    !Number.isInteger(quantity) ||
                    quantity < 1 ||
                    quantity > 1000000
                ) {
                    displayMessage(
                        form,
                        'Quantity must be a whole number ' +
                        'between 1 and 1,000,000.',
                        false
                    );

                    return;
                }

                // Validate deadline.
                if (
                    !/^\d{4}-\d{2}-\d{2}$/.test(deadline) ||
                    deadline < getLocalToday()
                ) {
                    displayMessage(
                        form,
                        'Please select a valid deadline ' +
                        'that is not in the past.',
                        false
                    );

                    return;
                }

                // Validate specifications.
                if (
                    specifications.length < 10 ||
                    specifications.length > 10000
                ) {
                    displayMessage(
                        form,
                        'Specifications must contain between ' +
                        '10 and 10,000 characters.',
                        false
                    );

                    return;
                }

                // Prepare quotation request.
                const quotation = {
                    fullName: getField(form, 'fullname'),

                    company:
                        getField(form, 'company') || null,

                    email: getField(form, 'email'),

                    phone: getField(form, 'phone'),

                    category: getField(form, 'category'),

                    quantity: quantity,

                    deadline: deadline + 'T12:00:00',

                    specifications: specifications
                };

                submitting = true;
                setSubmitting(form, true);

                try {
                    const response = await fetch(
                        API_BASE_URL +
                        '/api/CustomerQuotations',
                        {
                            method: 'POST',
                            mode: 'cors',

                            headers: {
                                'Content-Type':
                                    'application/json',

                                'Accept':
                                    'application/json'
                            },

                            body: JSON.stringify(quotation)
                        }
                    );

                    const responseText =
                        await response.text();

                    let data = null;

                    if (responseText) {
                        try {
                            data = JSON.parse(responseText);

                        } catch (parseError) {
                            console.warn(
                                'Unexpected API response format.',
                                parseError
                            );
                        }
                    }

                    if (!response.ok) {
                        throw new Error(
                            getApiError(
                                data,
                                response.status
                            )
                        );
                    }

                    if (!data || data.success !== true) {
                        throw new Error(
                            'The server returned an unexpected ' +
                            'response. Please contact the factory ' +
                            'before submitting again.'
                        );
                    }

                    // Display reference and private PIN.
                    displayQuotationConfirmation(
                        form,
                        data
                    );

                    // Reset form only after success.
                    form.reset();

                    if (deadlineInput) {
                        deadlineInput.min = getLocalToday();
                    }

                } catch (error) {
                    console.error(
                        'Quotation submission error:',
                        error
                    );

                    const message =
                        error instanceof TypeError
                            ? 'Unable to connect to the server. ' +
                              'Please check your internet ' +
                              'connection and try again.'
                            : error.message ||
                              'An unexpected error occurred.';

                    displayMessage(
                        form,
                        message,
                        false
                    );

                } finally {
                    submitting = false;
                    setSubmitting(form, false);
                }
            }
        );
    }

    // ==========================================
    // GENERAL CONTACT FORM
    // ==========================================

    function initializeContactForm() {
        const form =
            document.getElementById('contactForm');

        if (!form) return;

        form.addEventListener(
            'submit',
            function (event) {
                event.preventDefault();

                clearMessage(form);

                if (isHoneypotFilled(form)) {
                    displayMessage(
                        form,
                        'Your message could not be submitted.',
                        false
                    );

                    return;
                }

                if (!form.checkValidity()) {
                    form.reportValidity();
                    return;
                }

                if (
                    typeof window.validateContactForm ===
                    'function' &&
                    !window.validateContactForm('contactForm')
                ) {
                    displayMessage(
                        form,
                        'Please correct the highlighted fields.',
                        false
                    );

                    return;
                }

                const firstName =
                    getField(form, 'firstname');

                const lastName =
                    getField(form, 'lastname');

                const phone =
                    getField(form, 'phone');

                const email =
                    getField(form, 'email');

                const inquiryType =
                    getField(form, 'inquiry_type');

                const message =
                    getField(form, 'message');

                const subject =
                    'QA Clothing Factory - ' +
                    inquiryType;

                const body = [
                    'Hello QA Clothing Factory,',
                    '',
                    message,
                    '',
                    'Customer Details',
                    '-------------------------',
                    'Name: ' +
                        firstName + ' ' + lastName,
                    'Email: ' + email,
                    'Phone: ' + phone,
                    'Inquiry Type: ' + inquiryType,
                    '',
                    'Kind regards,',
                    firstName + ' ' + lastName
                ].join('\n');

                const mailtoUrl =
                    'mailto:sales@clothingfactory.co' +
                    '?subject=' +
                    encodeURIComponent(subject) +
                    '&body=' +
                    encodeURIComponent(body);

                displayMessage(
                    form,
                    'Your email application will open ' +
                    'with your message prepared. ' +
                    'Please press Send in your email ' +
                    'application to deliver the message.',
                    true
                );

                window.location.href = mailtoUrl;
            }
        );
    }
})();
