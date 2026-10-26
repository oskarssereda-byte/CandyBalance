# CandyBalance 1.0.2 build project

This project is configured to compile against Paper API 1.21.11 and Java 21.

## Build online with GitHub Actions (no Maven installation needed)
1. Create a new GitHub repository.
2. Upload the contents of this folder (not the ZIP itself) into the repository.
3. Open the repository's **Actions** tab and enable Actions if prompted.
4. Choose **Build CandyBalance** and click **Run workflow** (or push/commit the files).
5. Open the completed workflow run, scroll to **Artifacts**, and download `CandyBalance-1.0.2`.
6. Extract the downloaded artifact ZIP; it contains `CandyBalance-1.0.2.jar`.

## Server installation
Back up your server first. Ensure Vault and an economy provider (for example EssentialsX Economy) are installed.
Stop the server, remove the old CandyBalance JAR, place the newly built JAR in `plugins`, then start the server.
Check the console for `CandyBalance enabled.` and confirm it is green in `/plugins`.

This source project has not been tested on a live server. If the build fails, share the GitHub Actions build log.
