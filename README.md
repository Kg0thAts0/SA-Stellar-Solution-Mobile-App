# SA-Stellar-Solution-Mobile-App
SA Stellar Solutions
QA Clothing Factory Management System

---

## Overview
The QA Clothing Factory Management System is a comprehensive digital solution developed as part of a Work Integrated Learning (WIL) project. The system was designed and implemented over the course of an academic year to modernise and streamline the operations of a clothing manufacturing business that still makes use of manual processing systems to manage and streamline their business operations. The system has been designed with the main goal of eliminating paper-based processes and to digitalise the business' processes.

The solution consists of three fully integrated platforms:

1.  **Customer Website**
2.  **Mobile Application**
3.  **Web Application**

Together, these platforms enable customers to submit quote requests, monitor order requirements, and communicate with the factory, while administrative users can efficiently manage quotations, customer requests, production-related information, and business operations through dedicated management interfaces.

---

## Project Objectives
The primary objective of the project was to create a centralised system capable of improving operational efficiency within a clothing manufacturing environment by:

-   Digitising the quotation request process.
-   Improving communication between customers and factory staff.
-   Reducing manual paperwork and data duplication.
-   Providing administrators with tools for managing customer requests.
-   Creating a scalable and user-friendly solution across multiple platforms.
-   Enhancing customer experience through accessible digital services.

---

## System Architecture
The QA Clothing Factory Management System is composed of three interconnected components that work together to achieve a seamless business workflow.

### 1. Customer Website
The website serves as the public-facing platform where customers can interact with the business.

**Features**
-   User-friendly interface
-   Company information and service details
-   Product and service showcases
-   Online quote request submissions
-   Order requirement submissions
-   Customer enquiry management
-   Contact and communication functionality

**Purpose**
The website acts as the primary entry point into the system, allowing customers to submit information that is automatically made available to administrators through the management applications.

### 2. Mobile Application
The mobile application provides administrative staff with the ability to manage and process customer requests while on the move.


**Features**
-   Secure authentication
-   Quote management
-   Request processing
-   Customer information management
-   Status updates
-   Real-time access to submitted requests
-   Mobile-friendly user experience

**Purpose**
The mobile application ensures that authorised personnel can continue business operations from anywhere, enabling efficient and flexible management of factory-related requests.

### 3. Web Application
The web application serves as the primary administrative management platform.

**Features**
-   Dashboard and analytics
-   Quote processing and management
-   Customer management
-   Order tracking
-   Administrative controls
-   Data management and reporting
-   Workflow monitoring

**Purpose**
The web application provides a comprehensive environment for managing all customer requests received through the website and facilitating operational decision-making.

---

## System Workflow
1.  Customer accesses the website.
2.  Customer submits a quotation request or order enquiry.
3.  Information is stored within the central database.
4.  Requests become available to authorised users.
5.  Administrators review and process submissions using either:
    -   Mobile Application
    -   Web Application
6.  Quote status and customer information are updated.
7.  Business operations proceed based on approved requests.

---

## Technologies Utilised
The project incorporated modern software development technologies and best practices.

### Frontend Technologies
-   HTML5
-   CSS3
-   JavaScript
-   Responsive Design Principles

### Mobile Development
-   Android Development Technologies
-   Mobile User Interface Design

### Backend Technologies
-   Server-side Processing
-   Business Logic Implementation
-   RESTful Communication

### Database Management
-   Relational Database Systems
-   Data Validation
-   Data Persistence

### Development Tools
-   Version Control Systems
-   Testing Tools
-   Project Management Practices

### API & Hosting
-   **API base URL:** `https://your-api-base-url.example.com`
-   **Hosting provider:** `<Firebase / Azure / AWS>`
-   **Database:** `<MySQL / PostgreSQL / Firestore>`

---

## Key Functional Requirements
The system was designed to satisfy the following requirements:

-   User authentication and authorisation
-   Quote submission and management
-   Customer data management
-   Order information processing
-   Request tracking
-   Administrative reporting
-   Data security and validation
-   Multi-platform access


## Non-Functional Requirements
The system was developed with consideration for:

-   Performance
-   Reliability
-   Security
-   Scalability
-   Maintainability
-   Usability
-   Availability

---

## Design Considerations
The QA Clothing Factory Management System was designed with careful consideration of the operational challenges experienced by clothing manufacturing businesses that rely heavily on manual processes.

### User-Centred Design
The system was designed with a user-centric approach, ensuring that both factory administrators (internal users) and customers (external users) could navigate the platforms intuitively.

-   **Identification of User Groups:** The team identified two distinct user groups during the requirements gathering phase: Customers (wholesalers, retailers, and trade-only companies) who need a simple way to request quotes and view products, and Administrative Staff (factory managers and production planners) who need a robust dashboard to process requests, manage orders, and track production status.
-   **Ease of Use:** The interfaces were designed to minimise training requirements. The customer website utilises a clear, minimal navigation bar with prominent "Request Quote" calls-to-action. The administrative web dashboard uses a sidebar layout with recognisable icons to reduce cognitive load.
-   **Accessibility Considerations:** The colour palette (black, white, and orange accents) ensures high contrast for readability. The mobile application utilises large tap targets and mobile-friendly typography to accommodate staff working in active factory environments.
-   **Navigation Design Decisions:** The customer website follows a linear flow (Home → Products → Request Quote), while the administrative platforms prioritise quick access to high-priority tasks (e.g., a "Pending Requests" queue on the main dashboard).

### Scalability
-   **Future Growth:** The database schema was designed with relational integrity to support the addition of new product categories, complex order workflows, and multiple factory branches without requiring a complete rebuild.
-   **Supporting Additional Users:** The backend RESTful API is decoupled from the frontend, allowing the system to handle an increasing number of concurrent users without significant performance degradation.
-   **System Expansion Possibilities:** The modular nature of the system allows for future modules (such as Inventory Management or Payment Gateways) to be integrated as standalone services that interact with the existing core system.

### Security
-   **Authentication Strategy:** The system enforces secure login procedures for all administrative users (Mobile and Web applications). Passwords are hashed and salted before database storage to prevent credential theft.
-   **Authorisation Controls:** Role-Based Access Control (RBAC) is implemented, ensuring that general administrative staff cannot access high-level financial data or system settings, while Program Managers and System Analysts retain full access.
-   **Data Protection Measures:** All data transmitted between the frontend clients and the backend server is encrypted using HTTPS/TLS protocols. Input sanitisation is applied to all customer submission forms to prevent SQL injection and Cross-Site Scripting (XSS) attacks.

### Performance
-   **Response Time Optimisation:** The API utilises efficient query structures and indexing on the database to ensure that customer quote requests load quickly on the administrative dashboard, even during peak submission times.
-   **Database Efficiency Considerations:** Normalisation techniques were applied to the database design to reduce data redundancy, and connection pooling is used to manage database connections efficiently.
-   **Application Performance Enhancements:** The mobile application utilises local caching strategies to display previously loaded request data immediately, while fetching new data in the background.

### Reliability and Availability
-   **Data Backup Approach:** Automated database backups are scheduled at regular intervals to prevent data loss in the event of a system failure.
-   **Error Handling:** The application includes comprehensive try-catch blocks in the backend and user-friendly error messages in the frontend to prevent application crashes and guide users during unexpected failures.
-   **System Availability Considerations:** Hosted on reliable cloud infrastructure, the system is designed to maintain high uptime, ensuring that customers can submit quote requests 24/7 and administrators can access the system whenever needed.

---

## GitHub Repository Management
GitHub played a vital role in the development of the QA Clothing Factory Management System by providing a centralised platform for source code management, version control, collaboration, and project tracking.

### Purpose of Using GitHub
The project team utilised GitHub to:

-   Maintain source code versions.
-   Collaborate across multiple development team members.
-   Manage branches and feature development.
-   Merge approved changes into the main codebase.
-   Track project progress and issues.
-   Preserve a complete history of project development.

### Branching Strategy
The team adopted a structured branching strategy to manage code changes and facilitate collaboration:

-   **main:** The production-ready branch containing the latest stable release of the system.
-   **development:** The integration branch where features were merged and tested before being pushed to main.
-   **feature branches:** Individual branches created for specific tasks (e.g., `feature/quote-api`, `feature/mobile-ui`) to isolate new development.
-   **bug-fix branches:** Created to address specific issues discovered during testing (e.g., `bugfix/login-error`).

### Collaborative Workflow
Team collaboration was managed through GitHub Pull Requests (PRs). When a developer completed a feature on their branch, they submitted a PR to the `development` branch. This allowed other team members (specifically the Lead Developer and System Analyst) to review the code, provide feedback, and approve the changes before merging. This peer-review process ensured code quality and knowledge sharing across the team.

### Version Control Benefits
GitHub enabled the team to:

-   Prevent accidental code loss.
-   Track modifications and updates.
-   Review code contributions.
-   Support collaborative software development.

### GitHub Actions and Continuous Integration
GitHub Actions were implemented to automate development workflows and improve software quality throughout the project lifecycle.

**Purpose of GitHub Actions:**
The team utilised GitHub Actions to:

-   Automate builds.
-   Validate source code.
-   Execute automated tests.
-   Improve deployment consistency.
-   Ensure code quality before release.

**Workflow Overview**
The team configured a workflow that triggers on every push and pull request to the `main` branch. The workflow automatically sets up the build environment, installs dependencies, runs the unit test suite, and reports the build status. If any test fails, the merge is blocked, ensuring that broken code never reaches production.

**Benefits Achieved**
GitHub Actions helped the team:

-   Reduce manual work.
-   Detect issues early.
-   Improve code reliability.
-   Streamline development processes.






##Example Workflow
<img width="940" height="425" alt="image" src="https://github.com/user-attachments/assets/cf7a513a-cc45-4f68-980c-f73d04312bf0" />



 
The screenshot above shows the automated build and test workflow triggered on every push to the repository. It demonstrates the sequential steps of checking out the code, configuring the environment, running tests, and reporting results. A green check confirms all tests pass; a red cross blocks merges.
Team Contribution
This project was completed by the members of SA Stellar Solutions as part of a Work Integrated Learning programme.
The team collaborated throughout the entire software development lifecycle, including:
•	Requirements gathering
•	Analysis and planning
•	System design
•	Database design
•	User interface design
•	Software development
•	System testing
•	Documentation
•	Deployment preparation

Testing
Comprehensive testing was conducted throughout development to ensure system quality.
Testing Activities
•	Unit Testing
•	Integration Testing
•	System Testing
•	User Acceptance Testing
•	Functional Testing
•	Interface Testing
Issues discovered during testing were documented, corrected, and retested to ensure system stability and reliability.

Security Considerations
To protect organisational and customer information, the system incorporates:
•	User authentication
•	Access control mechanisms
•	Data validation
•	Secure database interactions
•	Error handling
•	Input sanitisation

Project Outcomes
The implementation of the QA Clothing Factory Management System successfully provides:
•	Improved customer engagement
•	Faster quotation processing
•	Better operational visibility
•	Centralised information management
•	Greater administrative efficiency
•	A modern digital presence for the clothing factory


Future Enhancements
Potential future improvements include:
•	Online payment integration
•	Production tracking functionality
•	Inventory management
•	Customer notifications
•	Business analytics dashboards
•	Advanced reporting features
•	Cloud deployment enhancements


Project Team
The QA Clothing Factory Management System was successfully designed and developed by SA Stellar Solutions as part of a Work Integrated Learning (WIL) programme. The project was completed through collaborative effort, with each team member contributing specialised skills throughout the Software Development Life Cycle (SDLC).
Team Members and Roles
Team Member	Role	Responsibility
Kgothatso Magwaza	Program Manager	Oversaw project planning, coordination, scheduling, project monitoring, risk management and stakeholder communication. Ensured project deliverables were completed within the required timeframe and maintained alignment with project objectives.
Thando Nkoane	System Analyst	Conducted requirements gathering, analysed business processes, documented system requirements, assisted with solution design, and ensured that the system met the operational needs of the clothing factory.
Nomndeni Mdakane	UI/UX Designer	Designed and improved the user experience across all platforms. Developed wireframes, user interface layouts, prototypes, and design standards to ensure accessibility, usability, and consistency.
Brian Mlambo	Frontend and Backend Developer	Responsible for developing core system functionality, implementing user interfaces, integrating backend services, managing data interactions, and supporting application deployment.
Sibusiso Mhlongo	Frontend and Backend Developer	Assisted in the development of both frontend and backend components, implemented business logic, supported database integration, conducted testing, and contributed to system optimisation.


Collaborative Contributions
As a team, SA Stellar Solutions participated in:
•	Project planning and management
•	Requirements elicitation and analysis
•	System architecture design
•	Database design and implementation
•	Frontend development
•	Backend development
•	Website development
•	Mobile application development
•	Web application development
•	User interface and user experience design
•	System integration
•	Testing and quality assurance
•	Documentation and reporting
•	Final project presentation and deployment preparation

The combination of these contributions enabled the successful delivery of a multi-platform Clothing Factory Management System that addresses the operational and administrative requirements of a modern clothing manufacturing business.

Conclusion
The QA Clothing Factory Management System demonstrates the successful development of an integrated multi-platform business solution. Through the combination of a customer-facing website, mobile application, and administrative web application, the project delivers an efficient and scalable approach to managing quotation requests, customer information, and clothing factory operations.
This project reflects the practical application of software engineering principles, teamwork, problem-solving, and modern development practices acquired throughout the Work Integrated Learning programme.





Developed By
SA Stellar Solutions
Work Integrated Learning (WIL) Project
Academic Year: 2026

Screenshots and System Demonstration
Customer Website


Home Page
<img width="940" height="453" alt="image" src="https://github.com/user-attachments/assets/34e203fd-b5f3-4cbf-b821-fbce08498b55" />

 *Figure 1: Website Home Page*






 



 Quote Request Page
 
 <img width="817" height="394" alt="image" src="https://github.com/user-attachments/assets/c4b8f749-112c-4854-832a-dc15d9bce3d8" />

Figure 2: Quote Request Form

Mobile Application 
Dashboard and Request Management

<img width="399" height="570" alt="image" src="https://github.com/user-attachments/assets/48770b01-ddb0-4e87-8291-972d23585853" />

 
Figure 3: Mobile Application Dashboard and Mobile Request Processing Screen

Administrative Web Application
Dashboard

<img width="312" height="690" alt="image" src="https://github.com/user-attachments/assets/9cee368e-7b8f-44e8-b8f9-d9b83ca6934d" />

 
Figure 5: Administrative Dashboard

Quote Management

<img width="728" height="409" alt="image" src="https://github.com/user-attachments/assets/d1fcd50a-32a6-4b97-81fc-9ccaeb7a8407" />
 
Figure 6: Quote Processing Interface
Reports

<img width="940" height="476" alt="image" src="https://github.com/user-attachments/assets/30080db2-2697-4eb0-b735-9fac4e3fe43b" />

Figure 7: Reporting Dashboard









Google Playstore Publishing

<img width="1920" height="1018" alt="aab-file" src="https://github.com/user-attachments/assets/e15cf9b2-f5eb-4668-bb9b-7710faff3c71" />

Figure 8: AAB file

<img width="1920" height="1017" alt="file-" src="https://github.com/user-attachments/assets/e8138b73-fe8b-4b8c-b822-71dbf5fd5d5d" />

Figure 9: File

<img width="1920" height="873" alt="Google" src="https://github.com/user-attachments/assets/83dd2d48-9ab2-4c9d-b6cb-2d3812af4913" />

Figure 10: Google

<img width="1920" height="1024" alt="release" src="https://github.com/user-attachments/assets/021d29d6-15c3-4957-8908-40c3fe64ac9f" />

Figure 11: Release

<img width="1920" height="1018" alt="aab-file-1" src="https://github.com/user-attachments/assets/537c8b53-3bb6-4d0d-9fde-c7b62765d609" />

Figure 12: App release aab 

---
##Link for Demonstration Video

Release Notes
Version 2.0 – Final WIL Release
Release Date: 16 November 2026
This version represents the final release of the QA Clothing Factory Management System and includes significant improvements made since the prototype phase.

Enhancements Since Prototype
User Interface Improvements
•	Role-specific dashboards: Each of the five roles (Production Manager, Supervisor, Inventory Clerk, Quality Controller, Administrator) now has a dedicated dashboard displaying only the modules relevant to their responsibilities.
•	Design refinements: Consistent typography, card-based layout, spacing, and navigation across all roles. Added splash/loading screen, dedicated Forgot Password and Reset Password screens, and a full Settings and Help & Support screen.
Performance Improvements
•	API optimisation: Introduced efficient query structures and database indexing so that standard API reads complete within the 3-second target (95th percentile) under defined test conditions.
•	Database optimisations: Applied normalisation to reduce data redundancy and implemented connection pooling to manage database connections efficiently. Mobile app now uses local caching to display previously loaded data immediately.
Security Enhancements
•	Security enhancements: Enforced HTTPS/TLS on all client-server communication, added input sanitisation on all customer submission forms, and implemented comprehensive audit logging of significant approvals, stock changes, and inspection outcomes (NFR-13).
•	 Authentication improvements: Passwords are hashed and salted before storage. Added Google Sign-In integration, "Remember Me" session persistence, and secure token-based password recovery with expiry (NFR-18). Server-side role-based access control now rejects cross-role API requests (NFR-06).
 Bug Fixes
•	Resolved session-persistence issue where "Remember Me" did not correctly restore sessions on app restart.
•	Fixed navigation bug where back-navigation from role dashboards returned to the wrong screen.
•	Corrected validation on the Add Raw Material form to reject duplicate material codes and invalid initial stock values.
•	Fixed quotation status not updating on the customer view after manager approval.
•	Resolved Android back-stack issue on the Reset Password flow causing the app to close unexpectedly.

Innovative Features Added
The following innovative features were introduced after completion of the prototype version:
Feature 1: End-to-End Integrated Factory Workflow
Description
A single quotation accepted by a customer now automatically flows through the entire system: customer submission → Production Manager review and pricing → customer acceptance → manager approval and production order creation → Inventory Clerk material issuance → Supervisor progress tracking → Quality Controller inspection → management reporting. Each stage updates the next role's dashboard in real time (FR-35).
Business Benefit
Eliminates manual handoffs and paper forms between departments. Managers can see the full lifecycle of any order, reducing delays, miscommunication, and data duplication across the factory.




Feature 2: Role-Based Access Control (RBAC) Across All Modules
Description
Every screen, API endpoint, and dashboard is gated by the authenticated employee's role. Five fully implemented roles each have distinct module access: Production Manager (FR-10 to FR-15), Supervisor (FR-16 to FR-19), Inventory Clerk (FR-20 to FR-24), Quality Controller (FR-25 to FR-27), and Administrator (FR-28 to FR-31). Server-side authorisation enforces permissions regardless of client behaviour (NFR-06).
Business Benefit
Protects sensitive factory and customer data from unauthorised access. Ensures each employee only sees what is relevant to their job, reducing cognitive load and improving security compliance.

Feature 3: Low-Stock Monitoring and Intelligent Inventory Management
Description
The Inventory Clerk dashboard now tracks every raw material against a defined reorder level. When available stock falls to or below the reorder level, the material is automatically flagged for replenishment (FR-24). Material requests from production are reviewed and issued directly, and every stock movement (receipt, issue, adjustment, return) is recorded with quantity and transaction details (FR-23).
Business Benefit
Prevents production stoppages caused by unnoticed stock shortages. Gives the factory real-time visibility of material availability and reduces waste caused by over-ordering or expired stock.

Additional Improvements
•	Splash/initialisation screen with QA Clothing Factory branding (FR-01).
•	Google Sign-In for employees who prefer it (FR-04).
•	Google account linking from within the app settings (FR-32).
•	Granular notification preferences allowing employees to toggle app-wide notifications and specific categories such as Production Updates, Quality Control, Inventory Alerts, and Supervisor Updates (FR-33).
•	In-app Help & Support with Getting Started guidance and role-specific help content (FR-34).
•	Automated testing pipeline via GitHub Actions running on every push to the main branch.


Known Limitations
•	Notification delivery is currently dependent on the employee granting Android notification permissions; some categories remain silent until permission is granted.
•	Customer-facing website currently supports quote request submission but does not yet support in-browser order tracking or real-time status visibility for customers.
•	Reports export to in-app views only — scheduled PDF/Excel export is planned for the next release.
•	Offline mode provides graceful error handling and retry states but does not yet support full offline data entry.

Future Release Roadmap
Planned future enhancements include:
- Inventory management.
- Online payment integration.
- Business intelligence reporting.
- Customer notification services.
- Production tracking.

