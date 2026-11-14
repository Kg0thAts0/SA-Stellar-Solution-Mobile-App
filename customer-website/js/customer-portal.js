/* QA Clothing Factory customer portal
 * Website-first stage: customer lookup and decision endpoints do not exist yet.
 * This file intentionally does NOT expose private quotation details or record
 * accept/decline actions until the API implements authenticated verification.
 */
(function () {
    'use strict';
    const params = new URLSearchParams(location.search);
    const referencePattern = /^QA-\d{8}-[A-Z0-9]{12}$/i;
    const reference = params.get('reference') || '';
    const input = document.getElementById('reference') || document.getElementById('decisionReference');
    if (input && referencePattern.test(reference)) input.value = reference.toUpperCase();

    const lookup = document.getElementById('portalLookup');
    if (lookup) lookup.addEventListener('submit', function (event) {
        event.preventDefault();
        const ref = lookup.elements.namedItem('reference').value.trim().toUpperCase();
        const email = lookup.elements.namedItem('email').value.trim();
        const message = document.getElementById('lookupMessage');
        message.hidden = false;
        message.classList.remove('error');
        if (!referencePattern.test(ref)) {
            message.textContent = 'Enter a valid quotation reference in the format QA-YYYYMMDD-XXXXXXXXXXXX.';
            message.classList.add('error');
            return;
        }
        if (!email || !lookup.elements.namedItem('email').checkValidity()) {
            message.textContent = 'Enter the email address you used when requesting the quotation.';
            message.classList.add('error');
            return;
        }
        // Never use a reference and email alone as authorization to return PII.
        // Server-side email ownership verification and rate limiting are required.
        message.textContent = 'The tracking page is ready, but secure quotation lookup is not yet connected to the API. No customer data has been retrieved. Your quotation submission form remains available on the Contact page.';
    });

    const decision = document.getElementById('decisionForm');
    if (decision) decision.addEventListener('submit', function (event) {
        event.preventDefault();
        const msg = document.getElementById('decisionMessage');
        const ref = decision.elements.namedItem('reference').value.trim();
        if (!referencePattern.test(ref)) {
            msg.textContent = 'Enter a valid quotation reference first.';
            msg.classList.add('error');
        } else if (!decision.elements.namedItem('email').checkValidity()) {
            msg.textContent = 'Enter a valid email address first.';
            msg.classList.add('error');
        } else {
            msg.textContent = 'Verification requests are not available until the secure API endpoint is implemented. No acceptance or rejection has been submitted.';
            msg.classList.remove('error');
        }
    });
})();
