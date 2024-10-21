# JavaFX Database Management Application

## Overview
This JavaFX application provides a user-friendly interface for managing a database of users. It includes features such as user authentication, CRUD operations (Create, Read, Update, Delete), profile picture management, and theme switching.

## Features
- **User Authentication**: Secure login and registration system.
- **Database Operations**: 
  - Add new users
  - Edit existing user information
  - Delete users
  - Query users by name
  - List all users
- **Profile Picture Management**: 
  - Upload profile pictures for users
  - Display profile pictures
  - Delete profile pictures
- **Theme Switching**: Toggle between light and dark themes.
- **User-Friendly Interface**: 
  - Table view for easy data visualization
  - Form for user data input/editing
  - Menu bar for quick access to functions
- **Splash Screen**: Displays on application startup.

## Technologies Used
- Java
- JavaFX for GUI
- MySQL Database (hosted on Azure)
- JDBC for database connectivity

## Setup and Installation
1. Ensure you have Java JDK 11 or later installed.
2. Clone this repository:
2. Clone this repository: https://github.com/Philippe-Je/JavaFxDB_SQL_ShellCode.git
3. Open the project in your preferred Java IDE (e.g., IntelliJ IDEA, Eclipse).
4. Set up the MySQL database:
- Create a MySQL database on Azure or your preferred hosting service.
- Update the `DB_URL`, `USERNAME`, and `PASSWORD` in the `ConnDbOps` class with your database credentials.
5. Run the `App` class to start the application.

## Usage
1. **Launch the Application**: Run the `App` class.
2. **Login/Register**: Use the authentication dialog to log in or register a new user.
3. **Main Interface**:
- Use the form on the left to input user data.
- The table on the right displays all users.
- Select a user in the table to populate the form for editing.
4. **Database Operations**:
- Click "Add" to insert a new user.
- Click "Edit" to update the selected user's information.
- Click "Delete" to remove the selected user.
5. **Profile Picture**:
- Click "Add Image" to upload a profile picture for a user.
- Click "Delete Image" to remove a user's profile picture.
6. **Theme Switching**: Use the "Toggle Theme" option in the View menu or press Ctrl+T.

## Menu Options
- **File**: 
- Close (Ctrl+X): Exit the application
- **Database**: 
- Connect to DB (Ctrl+C): Establish database connection
- **View**: 
- Toggle Theme (Ctrl+T): Switch between light and dark themes
- **Help**: 
- Challenges: View development challenges and solutions

## Development Challenges
The project faced several challenges during development, including:
1. Database connection issues with Azure MySQL.
2. JavaFX and FXML integration complexities.
3. Implementing a smooth theme switching mechanism.

Solutions to these challenges are detailed in the application's Help menu.

## Contributing
Contributions to improve the application are welcome. Please follow these steps:
1. Fork the repository.
2. Create a new branch for your feature.
3. Commit your changes.
4. Push to the branch.
5. Create a new Pull Request.


## Contact
Philippe Jean - phil09492@gmail.com

Project Link: [https://github.com/yourusername/javafx-database-management](https://github.com/Philippe-Je/JavaFxDB_SQL_ShellCode.git)
