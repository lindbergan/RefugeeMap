# RefugeeMap
*RefugeeMap* (tentative name) is an Android application developed as part of the course [**DAT255**](https://github.com/hburden/DAT255 "Course page") at *Chalmers University of Technology*.
The purpose of the application is to provide help for refugees in Sweden by presenting them with a map or list of events (of a variety of categories such as sports, educational help and various group events).
The database of events is downloaded by the application and stored locally to allow for offline use.
Each event contains a description as well as various other types of information such as an address and contact information.

## Configuration for Java code
All Java source files must use the following configuration:

| Newline | Encoding | Indentation | Max. characters per line    |
|:-------:|:--------:|:-----------:|:---------------------------:|
| LF      | UTF-8    | Two spaces  | 80                          |

## Rules for committing
Never commit directly to the `master` branch or any sprint branch.
Instead, create a *feature branch* for each task or subtask.
When creating a new feature branch, name it after the code of the corresponding task or subtask in **JIRA**, and also create an issue containing the name of the branch and a description of the feature.
When you're finished with a feature branch, resolve any merge conflicts and create a pull request to the active sprint branch.
If you do not close the corresponding issue, it will be closed by the administrator after merging.

Never commit to a branch created by someone else without their permission.

Any documents unrelated to the code should be uploaded to the `documents` branch, preferably written in plaintext or Markdown.

If you discover something that needs to be changed but is unrelated to a feature branch, such as `.gitignore` or a project configuration file, notify the administrator to have it changed in the active sprint branch.
