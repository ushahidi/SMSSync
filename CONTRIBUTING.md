## Development

If you would like to contribute code to SMSSync you can do so by forking the [repository][1] and sending a pull request. We will review your code. If everything with the pull request looks good, we will humbly merge your changes.

### Branch structure

The repository is made up of three main branches: **master (stable)**, **develop (ustable)** and **release (quite stable and a temporary branch)**.

* **master** has the latest stable code, it's tags are released as [SMSSync][1] on the Google playstore.
* **develop** has the latest unstable code. It's codes are merge into master.
* **release** is a branch of develop after feature freeze. mainly for fine tuning and testing to get the code stable for a release. The changes made here are finally merged into develop then develop is merged into master branch. After which a version tag is created for a release.

Always, work with the `develop` branch.

Join the [developer mailing list][2] and let us hear what you're working on.

### File Issues

If you're exepriencing an issue and want to file it for the contributors to work on, you can do so by

1. Search the [Github issue tracker][3] to make sure your issue has not been filed already. If it has, please add comment to the existing issue to add more details to it.

2. Give a step by step guide on how to reproduce the issue. You can read our [guide][4] on how to file an issue on the [wiki][4].

**Note:** The more we know about the issue, the easier it's for us to fix it.

**Note:** For your contribution to be accepted into the project, you must sign the [Individual Contributor License Agreement (CLA)][5] You can read more about it there [here][6]

Thanks for supporting the SMSSync development team.

[1]: https://github.com/ushahidi/SMSSync
[2]: http://list.ushahidi.com
[3]: https://github.com/ushahidi/SMSSync/issues
[4]: https://wiki.ushahidi.com/display/WIKI/Report+a+bug
[5]: https://docs.google.com/forms/d/15LyeKTOP36T5u3290o4hsmqv79v-m_s2QSmgMJXpYp8/viewform
[6]: https://wiki.ushahidi.com/display/WIKI/Licenses
