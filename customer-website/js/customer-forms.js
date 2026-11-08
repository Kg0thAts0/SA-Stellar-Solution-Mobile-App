/*
 * QA Clothing Factory - Customer Forms
 *
 * Quotation requests:
 * POST /api/CustomerQuotations
 *
 * Contact messages:
 * Opens the customer's email application.
 *
 * This file works alongside main.js.
 */

(function () {
    "use strict";

    const API_BASE_URL =
        "https://sa-stellar-solution-mobile-app.onrender.com";

    document.addEventListener("DOMContentLoaded", function () {
        initializeQuotationForm();
        initializeContactForm();
    });

    // ==========================================
    // HELPER FUNCTIONS
    // ==========================================

    function getField(form, name) {
        const field = form.elements.namedItem(name);

        if (!field) {
            return "";
        }

        return String(field.value || "").trim();
    }

    function getLocalToday() {
        const now = new Date();

        const localDate = new Date(
            now.getTime() - now.getTimezoneOffset() * 60000
        );

        return localDate.toISOString().slice(0, 10);
    }

    function displayMessage(form, message, isSuccess) {
        let messageElement =
            form.querySelector(".form-api-message");

        if (!messageElement) {
            messageElement = document.createElement("div");

            messageElement.className = "form-api-message";

            messageElement.setAttribute("role", "status");

            messageElement.setAttribute(
                "aria-live",
                "polite"
            );

            messageElement.style.padding = "14px";
            messageElement.style.marginTop = "16px";
            messageElement.style.borderRadius = "8px";
            messageElement.style.fontSize = "14px";
            messageElement.style.lineHeight = "1.6";

            form.appendChild(messageElement);
        }

        messageElement.style.backgroundColor = isSuccess
            ? "#e8f5e9"
            : "#ffebee";

        messageElement.style.color = isSuccess
            ? "#1b5e20"
            : "#b71c1c";

        messageElement.style.border = isSuccess
            ? "1px solid #a5d6a7"
            : "1px solid #ef9a9a";

        messageElement.textContent = message;
    }

    function clearMessage(form) {
        const messageElement =
            form.querySelector(".form-api-message");

        if (messageElement) {
            messageElement.remove();
        }
    }

    function setSubmitting(form, submitting) {
        const submitButton =
            form.querySelector('button[type="submit"]');

        if (!submitButton) {
            return;
        }

        if (submitting) {
            submitButton.dataset.originalHtml =
                submitButton.innerHTML;

            submitButton.disabled = true;
            submitButton.style.opacity = "0.65";
            submitButton.style.cursor = "wait";

            submitButton.textContent = "Submitting...";
        } else {
            submitButton.disabled = false;
            submitButton.style.opacity = "";
            submitButton.style.cursor = "";

            if (submitButton.dataset.originalHtml) {
                submitButton.innerHTML =
                    submitButton.dataset.originalHtml;
            }
        }
    }

    function isHoneypotFilled(form) {
        const honeypot =
            form.elements.namedItem("website");

        return Boolean(
            honeypot &&
            String(honeypot.value || "").trim()
        );
    }

    function getApiError(data, statusCode) {
        if (data && typeof data.message === "string") {
            return data.message;
        }

        if (data && typeof data.title === "string") {
            return data.title;
        }

        if (data && data.errors) {
            const messages = Object.values(data.errors)
                .flat()
                .map(String);

            if (messages.length > 0) {
                return messages.join(" ");
            }
        }

        return (
            "The server could not process your request. " +
            "HTTP status: " + statusCode
        );
    }

    // ==========================================
    // QUOTATION REQUEST FORM
    // ==========================================

    function initializeQuotationForm() {
        const form = document.getElementById("quoteForm");

        if (!form) {
            return;
        }

        const deadlineInput =
            document.getElementById("quote_deadline");

        if (deadlineInput) {
            deadlineInput.min = getLocalToday();
        }

        let submitting = false;

        form.addEventListener("submit", async function (event) {
            event.preventDefault();

            if (submitting) {
                return;
            }

            clearMessage(form);

            // Preserve the honeypot created by main.js.
            if (isHoneypotFilled(form)) {
                displayMessage(
                    form,
                    "Your request could not be submitted.",
                    false
                );
                return;
            }

            if (!form.checkValidity()) {
                form.reportValidity();
                return;
            }

            if (
                typeof window.validateContactForm === "function" &&
                !window.validateContactForm("quoteForm")
            ) {
                displayMessage(
                    form,
                    "Please correct the highlighted fields.",
                    false
                );
                return;
            }

            const quantityValue =
                Number(getField(form, "quantity"));

            const deadlineValue =
                getField(form, "deadline");

            const specifications =
                getField(form, "specifications");

            if (
                !Number.isInteger(quantityValue) ||
                quantityValue < 1 ||
                quantityValue > 1000000
            ) {
                displayMessage(
                    form,
                    "Quantity must be a whole number between " +
                    "1 and 1,000,000.",
                    false
                );
                return;
            }

            if (
                !/^\d{4}-\d{2}-\d{2}$/.test(deadlineValue) ||
                deadlineValue < getLocalToday()
            ) {
                displayMessage(
                    form,
                    "Please select a valid deadline " +
                    "that is not in the past.",
                    false
                );
                return;
            }

            if (
                specifications.length < 10 ||
                specifications.length > 10000
            ) {
                displayMessage(
                    form,
                    "Specifications must contain between " +
                    "10 and 10,000 characters.",
                    false
                );
                return;
            }

            const quotation = {
                fullName: getField(form, "fullname"),
                company: getField(form, "company") || null,
                email: getField(form, "email"),
                phone: getField(form, "phone"),
                category: getField(form, "category"),
                quantity: quantityValue,
                deadline: deadlineValue + "T12:00:00",
                specifications: specifications
            };

            submitting = true;
            setSubmitting(form, true);

            try {
                const response = await fetch(
                    API_BASE_URL + "/api/CustomerQuotations",
                    {
                        method: "POST",
                        mode: "cors",
                        headers: {
                            "Content-Type": "application/json",
                            "Accept": "application/json"
                        },
                        body: JSON.stringify(quotation)
                    }
                );

                const responseText = await response.text();

                let data = null;

                if (responseText) {
                    try {
                        data = JSON.parse(responseText);
                    } catch (parseError) {
                        console.warn(
                            "Unexpected API response format.",
                            parseError
                        );
                    }
                }

                if (!response.ok) {
                    throw new Error(
                        getApiError(data, response.status)
                    );
                }

                if (!data || data.success !== true) {
                    throw new Error(
                        "The server returned an unexpected response. " +
                        "Please contact the factory before submitting again."
                    );
                }

                const referenceNumber =
                    data.referenceNumber || data.quotationID;

                let successMessage =
                    "Your quotation request was submitted successfully!";

                if (referenceNumber) {
                    successMessage +=
                        " Your reference number is: " +
                        referenceNumber + ".";
                }

                successMessage +=
                    " Please keep this reference for future inquiries.";

                displayMessage(form, successMessage, true);

                form.reset();

                if (deadlineInput) {
                    deadlineInput.min = getLocalToday();
                }

            } catch (error) {
                console.error(
                    "Quotation submission error:",
                    error
                );

                const message =
                    error instanceof TypeError
                        ? "Unable to connect to the server. " +
                          "Please check your internet connection " +
                          "and try again."
                        : error.message ||
                          "An unexpected error occurred.";

                displayMessage(form, message, false);

            } finally {
                submitting = false;
                setSubmitting(form, false);
            }
        });
    }

    // ==========================================
    // GENERAL CONTACT FORM
    // ==========================================

    function initializeContactForm() {
        const form = document.getElementById("contactForm");

        if (!form) {
            return;
        }

        form.addEventListener("submit", function (event) {
            event.preventDefault();

            clearMessage(form);

            if (isHoneypotFilled(form)) {
                displayMessage(
                    form,
                    "Your message could not be submitted.",
                    false
                );
                return;
            }

            if (!form.checkValidity()) {
                form.reportValidity();
                return;
            }

            if (
                typeof window.validateContactForm === "function" &&
                !window.validateContactForm("contactForm")
            ) {
                displayMessage(
                    form,
                    "Please correct the highlighted fields.",
                    false
                );
                return;
            }

            const firstName =
                getField(form, "firstname");

            const lastName =
                getField(form, "lastname");

            const phone =
                getField(form, "phone");

            const email =
                getField(form, "email");

            const inquiryType =
                getField(form, "inquiry_type");

            const message =
                getField(form, "message");

            const subject =
                "QA Clothing Factory - " + inquiryType;

            const body = [
                "Hello QA Clothing Factory,",
                "",
                message,
                "",
                "Customer Details",
                "-------------------------",
                "Name: " + firstName + " " + lastName,
                "Email: " + email,
                "Phone: " + phone,
                "Inquiry Type: " + inquiryType,
                "",
                "Kind regards,",
                firstName + " " + lastName
            ].join("\n");

            const mailtoUrl =
                "mailto:sales@clothingfactory.co" +
                "?subject=" + encodeURIComponent(subject) +
                "&body=" + encodeURIComponent(body);

            displayMessage(
                form,
                "Your email application will open with your " +
                "message prepared. Please press Send in your " +
                "email application to deliver the message.",
                true
            );

            window.location.href = mailtoUrl;
        });
    }
})();
