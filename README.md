# RefugeeMap
*Momtee* (working name *RefugeeMap*) is an Android application developed as part of the course [**DAT255**](https://github.com/hburden/DAT255 "Course page") at *Chalmers University of Technology*.
The purpose of the application is to provide help for refugees in Sweden by presenting them with a map or list of events (of a variety of categories such as sports, educational help and various group events).
The database of events is downloaded by the application and stored locally to allow for offline use.
Each event contains a description as well as various other types of information such as an address and contact information.

## Configuration for Java code
All Java source files must use the following configuration:

| Newline | Encoding | Indentation | Max. characters per line    |
|:-------:|:--------:|:-----------:|:---------------------------:|
| LF      | UTF-8    | One tab     | 80                          |

## Rules for committing
Never commit directly to the `master` branch or any sprint branch.
Instead, create a *feature branch* for each task or subtask.
When creating a new feature branch, name it after the code of the corresponding task or subtask in **JIRA**, and also create an issue containing the name of the branch and a description of the feature.
When you're finished with a feature branch, resolve any merge conflicts and create a pull request to the active sprint branch.
If you do not close the corresponding issue, it will be closed by the administrator after merging.

Never commit to a branch created by someone else without their permission.

Any documents unrelated to the code should be uploaded to the `documents` branch, preferably written in Markdown.

If you discover something that needs to be changed but is unrelated to a feature branch, such as `.gitignore` or a project configuration file, notify the administrator to have it changed in the active sprint branch.

## Project roles
[Sebastian](https://github.com/sebbehebbe) is the *Scrum master*, with the responsibility of organizing and tracking the team's actions in regard to the Scrum development process (including, among other tasks, being the administrator of the JIRA project and taking notes during meetings).

[Axel](https://github.com/drualsk) is the Git administrator, in charge of maintaining the structure of the Git repository (including tasks such as organizing the general flow of commits, ensuring the completeness of the `master` branch, managing the features added to in the active sprint branch and updating the project's `README` file).

All members actively contribute to the project in various ways - by adding code, collaborating on documentation and assisting other group members when problems occur.

## Important notes for measuring contributions
After the first sprint, the whitespace used for each level of indentation in Java files was changed from *two spaces* (two `' '`) to *one tab character* (one `'\t'`). As such, when using Git's `blame` command to find out which user added what to a file, make sure to use the `-w` option to ensure that changes in whitespace are ignored. (It seems like **gitinspector** does this by default.)
