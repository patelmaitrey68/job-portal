# How to Run the SmartHire AI Portal in VSCode

To run this project locally, you need to run both the **Backend (Java/Spring Boot)** and the **Frontend (React)** at the same time. 

Follow these simple steps:

## Step 1: Open the Project
1. Open VSCode.
2. Go to `File` > `Open Folder...`
3. Select the `job_portal` folder: `/Users/maitreypatel/Downloads/job_portal`

## Step 2: Start the Backend (Terminal 1)
The backend runs the API, the Database connection, and the AI Engine.

1. Open a new terminal in VSCode (`Terminal` > `New Terminal`).
2. Make sure you are in the root folder (`job_portal`).
3. Run this Maven command to start the Spring Boot server:
   ```bash
   mvn clean compile spring-boot:run -DskipTests
   ```
4. Wait until you see `Started JobPortalApplication` and `Tomcat started on port 8080`. Leave this terminal open!

*(Note: Ensure your PostgreSQL database `jobportaldb` is running on your Mac before starting the backend).*

## Step 3: Start the Frontend (Terminal 2)
The frontend runs the React User Interface.

1. Open a **second** terminal in VSCode (Click the `+` icon in the terminal panel to open a split terminal or a new one).
2. Change into the frontend directory by running:
   ```bash
   cd frontend
   ```
3. Run this command to start the Vite development server:
   ```bash
   npm run dev
   ```
4. It will take less than a second to start. Look for `Local: http://localhost:5173/`.

## Step 4: Open the App
1. Open your web browser (Chrome/Safari).
2. Go to **[http://localhost:5173](http://localhost:5173)**
3. You're all set! 

---

### Optional: Enabling Real AI
Currently, the app uses a "Mocked" AI so it won't crash if you don't have an API key. 
If you want to use the REAL Google Gemini AI:
1. Export your API key in your terminal before running the backend:
   ```bash
   export GEMINI_API_KEY=your_real_api_key_here
   mvn spring-boot:run -DskipTests
   ```
   Or, temporarily change it back in `main/resources/application.properties`.
