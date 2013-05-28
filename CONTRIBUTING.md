## Development

If you would like to contribute code to Ushahidi Android app you can do so by forking the [repository][1] and sending a pull request. We will review your code. If everything with the pull request looks good, we will humbly merge your changes.

### Branch structure

The repository is made up of three main branches: **master (stable)**, **develop (ustable)** and **release (quite stable and a temporary branch)**.

* **master** has the latest stable code, it's tags are released as [Ushahidi Android][1] on the Google playstore.
* **develop** has the latest unstable code. It's codes are merge into master.
* **release** is a branch of develop after feature freeze. mainly for fine tuning and testing to get the code stable for a release. The changes made here are finally merged into develop then develop is merged into master branch. After which a version tag is created for a release.

Always, work with the `develop` branch.

Join the [developer mailing list][2] and let us hear what you're working on.

### File Issues

If you're exepriencing an issue and want to file it for the contributors to work on, you can do so by

1. Search the [Github issue tracker][3] to make sure your issue has not been filed already. If it has, please add comment to the existing issue to add more details to it.

2. Give a step by step guide on how to reproduce the issue. You can read our [guide][4] on how to file an issue on the [wiki][4]. 

**Note:** The more we know about the issue, the easier it's for us to fix it.

Thanks for supporting the Ushahidi Android app development team.

[1]: https://github.com/ushahidi/Ushahidi_Android
[2]: http://list.ushahidi.com
[3]: https://github.com/ushahidi/Ushahidi_Android/issues
[4]: https://wiki.ushahidi.com/display/WIKI/Report+a+bug
