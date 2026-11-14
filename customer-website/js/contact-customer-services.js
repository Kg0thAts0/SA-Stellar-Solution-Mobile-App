/* Contact-page customer service panels. No customer data is retrieved or modified until secure backend routes are connected. */
(function () {
  'use strict';
  const buttons = Array.from(document.querySelectorAll('[data-customer-panel]'));
  const panels = Array.from(document.querySelectorAll('.customer-service-panel'));
  const pattern = /^QA-\d{8}-[A-Z0-9]{12}$/i;
  function openPanel(name, focus) {
    buttons.forEach(button => {
      const active = button.dataset.customerPanel === name;
      button.setAttribute('aria-expanded', String(active));
      button.classList.toggle('is-active', active);
    });
    panels.forEach(panel => { panel.hidden = panel.id !== 'customer-panel-' + name; });
    if (name && focus) document.getElementById('customer-panel-' + name)?.querySelector('input')?.focus();
  }
  buttons.forEach(button => button.addEventListener('click', () => {
    const next = button.getAttribute('aria-expanded') === 'true' ? '' : button.dataset.customerPanel;
    openPanel(next, true);
  }));
  document.querySelectorAll('[data-customer-form]').forEach(form => form.addEventListener('submit', event => {
    event.preventDefault();
    const reference = form.elements.namedItem('reference').value.trim().toUpperCase();
    const email = form.elements.namedItem('email');
    const message = form.querySelector('.customer-service-message');
    message.hidden = false;
    if (!pattern.test(reference)) {
      message.textContent = 'Enter a valid reference in the format QA-YYYYMMDD-XXXXXXXXXXXX.';
    } else if (!email.value.trim() || !email.checkValidity()) {
      message.textContent = 'Enter the email address used for your quotation request.';
    } else {
      message.textContent = form.dataset.customerForm === 'decision'
        ? 'Secure verification and quotation decisions are not yet connected. No decision has been recorded.'
        : 'Secure customer tracking is not yet connected to the API. No customer information has been retrieved.';
    }
  }));
  const params = new URLSearchParams(window.location.search);
  const reference = params.get('reference') || '';
  const section = params.get('service');
  if (['quote','order','decision'].includes(section)) {
    openPanel(section, false);
    const field = document.querySelector('#customer-panel-' + section + ' input[name="reference"]');
    if (field && pattern.test(reference)) field.value = reference.toUpperCase();
  }
})();
