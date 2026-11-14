/*
 * QA Clothing Factory - Customer Services Navigation
 *
 * Directs customers from the Contact page to the
 * dedicated quotation tracking, order tracking,
 * and quotation decision pages.
 */

(function () {
    "use strict";

    const buttons = Array.from(
        document.querySelectorAll("[data-customer-panel]")
    );

    const panels = Array.from(
        document.querySelectorAll(".customer-service-panel")
    );

    const referencePattern =
        /^QA-\d{8}-[A-Z0-9]{12}$/i;

    const destinations = {
        quote: "track-quotation.html",
        order: "track-order.html",
        decision: "quotation-decision.html"
    };

    function openPanel(name, focus) {
        buttons.forEach(function (button) {
            const active =
                button.dataset.customerPanel === name;

            button.setAttribute(
                "aria-expanded",
                String(active)
            );

            button.classList.toggle(
                "is-active",
                active
            );
        });

        panels.forEach(function (panel) {
            panel.hidden =
                panel.id !== "customer-panel-" + name;
        });

        if (name && focus) {
            const panel = document.getElementById(
                "customer-panel-" + name
            );

            panel?.querySelector("input")?.focus();
        }
    }

    buttons.forEach(function (button) {
        button.addEventListener("click", function () {
            const name = button.dataset.customerPanel;

            const expanded =
                button.getAttribute("aria-expanded") === "true";

            openPanel(expanded ? "" : name, true);
        });
    });

    document.querySelectorAll(
        "[data-customer-form]"
    ).forEach(function (form) {
        form.addEventListener("submit", function (event) {
            event.preventDefault();

            const type = form.dataset.customerForm;
            const destination = destinations[type];

            const referenceField =
                form.elements.namedItem("reference");

            const reference = String(
                referenceField?.value || ""
            ).trim().toUpperCase();

            const message = form.querySelector(
                ".customer-service-message"
            );

            if (!destination) {
                return;
            }

            if (!referencePattern.test(reference)) {
                if (message) {
                    message.hidden = false;
                    message.textContent =
                        "Enter a valid quotation reference.";
                }
                return;
            }

            const url = new URL(
                destination,
                window.location.href
            );

            url.searchParams.set(
                "reference",
                reference
            );

            window.location.assign(url.toString());
        });
    });

    const params = new URLSearchParams(
        window.location.search
    );

    const section = params.get("service");
    const reference = params.get("reference") || "";

    if (Object.prototype.hasOwnProperty.call(
        destinations,
        section
    )) {
        openPanel(section, false);

        const panel = document.getElementById(
            "customer-panel-" + section
        );

        const field = panel?.querySelector(
            'input[name="reference"]'
        );

        if (field && referencePattern.test(reference)) {
            field.value = reference.toUpperCase();
        }
    }
})();
