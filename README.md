# SMSSync

Welcome to *"SMSSync"*, an android application that turns your android powered phone into an SMS gateway.

Read about it at **[smssync.ushahidi.com](http://smssync.ushahidi.com/)**.

## Installation

Insallation and configuration details are [here][1].

## Development

Documentation for developers can be found [here][2].

## Contributing

If you would like to contribute code to SMSSync you can do so through [GitHub][3] by forking the repository and sending a pull request. We will review your code. If everything with the pull request looks good, we will humbly merge your changes.

### Branch structure

The repository is made up of three main branches: **master (stable)**, **develop (ustable)** and **release (quite stable and a temporary branch)**.

* **master** has the latest stable code, its tags are released as [SMSSync][4] on the Google playstore.
* **develop** has the latest unstable code. Its codes are merge into master after they become stable and well tested.
* **release** is a branch of develop after feature freeze. mainly for fine tuning and testing to get the code stable for a release. The changes made here are finally merged into both develop and master branches. After which master is tagged for a release.

[1]: http://smssync.ushahidi.com/howto
[2]: http://smssync.ushahidi.com/doc
[3]: https://github.com/ushahidi/SMSSync
[4]: https://play.google.com/store/apps/details?id=org.addhen.smssync
