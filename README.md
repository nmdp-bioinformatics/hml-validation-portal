# hml-validation-portal

Portal for HML validation - where users can upload HML files and choose from a variety of validation options.
1. Use HML gateway validator
2. Miring Validator
3. Gl-String Validation (created by Bob)
etc.


The code is in two parts. One UI Dashboard module and a backend Rest API module which is right now also the root project module.

The UI uses Angular 8 and is a basic UI right now with some issues and features yet to be completed - 
1. Connecting successfully to the backend
2. Parsing the response from validation services
3. Gl-String Sanity Check and Gl String validator checkboxes need separation from Gateway and Miring
4. Upload file only for HmlGateway and Miring
5. Input gl-string for sanity-check and Gl-string validation


The Backend uses OpenJDk8 and Maven can be built by simply using "mvn clean install". Items to still get done in Backend -
1. Connect to glstring validator
2. Connect to gl-string Sanity Check
3. Format response for each validation before sending response back to UI
4. Connect to AWS S3 for file-storage and response storage

These are broad items and more detailed descriptions will be added to these items in github (will be listed as issues).

Steps to run on your computer
1. Download the project using "git clone" command. 
2. To start the backend simply do "mvn clean install". And follow that by calling "java -jar hml-validation-portal.jar" from the target folder.
3. To start the UI, simply go to the hmlportaldashboard folder and run "ng serve --open" command in the terminal
