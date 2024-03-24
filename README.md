<a name="readme-top"></a>

[![Contributors][contributors-shield]][contributors-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

<div>

<h3 align="center">Load testing a very simple site with Gatling</h3>

  <p align="center">
    Gatling load testing scenarios written in Java language and run against a system under test, which is a very simple stack overflow like site
    <br />
    <a href="https://github.com/arpdkvcs/stackoverflow-load-test/issues">Report Bug</a>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
         <li><a href="#purpose">Purpose</a></li>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#setup-and-run">Setup & Run</a></li>
      </ul>
    </li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project
The load testing is divided mainly to two distinct part:
1) Test the sign up process (it is also used to have registered users on the site, to be able to test user dependent scenarios)
2) Test the other scenarios:
   - Posting questions
   - Posting answers to questions
   - Browsing, searching questions
   - Deleting questions

<p align="right">(<a href="#readme-top">back to top</a>)</p>


### Purpose
It is pet project of mine to deepen my understanding of the Gatling load testing tool.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Built With

* [![Gatling][Gatling]][Gatling-url]
* [![Java][Java]][Java-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started


### Prerequisites
1) Gatling supports 64bits OpenJDK LTS (Long Term Support) versions: 11, 17 and 21.
You need one of these installed on your computer: https://jdk.java.net/
2) The system under test: [Stack overflow][System-Under-Test-url] dummy site to be cloned and run (find instructions in it's README)

### Setup and Run

_Steps:_

1. Clone the repo
   ```sh
   git clone https://github.com/arpdkvcs/stackoverflow-load-test.git
   ```
2. Open the project in IntelliJ and trust the project
3. Run the `SignUp` scenario (inside repo folder)
    ```sh
    mvn gatling:test -Dgatling.simulationClass=stackoverflow.SignUpAPI
    ```
4. When the previous test finished, the run the GeneralUse test that contains different type of user scenarios:
   ```sh
    mvn gatling:test -Dgatling.simulationClass=stackoverflow.GeneralUse
    ```
At the end of each test it will generate a report which is accessible by it's link in the terminal or from the `/target/gatling/` folder. Choose the folder you want to see and open the `index.html` file in your browser. Clicking on it opens it in the default browser.

If you would like to have more or less users, you can use [my other pet project][Credential-Generator-url] which can generate credentials in `.csv` format. After that you have to place the generated file to the `src/test/resources/users` folder, then change the `CSV_FILENAME` variables value accordingly in each class (SignUpAPI/SignUpFrontend, GeneralUse).

DON'T RUN BOT `SignUpFrontend` AND `SignUpAPI` UNLESS YOU REBUILD THE WHOLE DATABASE OF THE SYSTEM UNDER TEST.
<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contact
[LinkedIn][Linkedin-url]

Project Link: [https://github.com/arpdkvcs/stackoverflow-load-test](https://github.com/arpdkvcs/stackoverflow-load-test)

<p align="right">(<a href="#readme-top">back to top</a>)</p>





<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/arpdkvcs/stackoverflow-load-test?style=for-the-badge
[contributors-url]: https://github.com/arpdkvcs/stackoverflow-load-test/graphs/contributors
[linkedin-shield]: https://img.shields.io/badge/LinkedIn-blue?style=for-the-badge
[linkedin-url]: https://www.linkedin.com/in/arpad-kovacs/
[Gatling]: https://img.shields.io/badge/Gatling-777777?style=for-the-badge&logo=gatling
[Gatling-url]: https://gatling.io/
[Java]: https://img.shields.io/badge/Java-777777?style=for-the-badge&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMjggMTI4IiBpZD0iamF2YSI+PHBhdGggZmlsbD0iIzAwNzRCRCIgZD0iTTQ3LjYxNyA5OC4xMnMtNC43NjcgMi43NzQgMy4zOTcgMy43MWM5Ljg5MiAxLjEzIDE0Ljk0Ny45NjggMjUuODQ1LTEuMDkyIDAgMCAyLjg3MSAxLjc5NSA2Ljg3MyAzLjM1MS0yNC40MzkgMTAuNDctNTUuMzA4LS42MDctMzYuMTE1LTUuOTY5ek00NC42MjkgODQuNDU1cy01LjM0OCAzLjk1OSAyLjgyMyA0LjgwNWMxMC41NjcgMS4wOTEgMTguOTEgMS4xOCAzMy4zNTQtMS42IDAgMCAxLjk5MyAyLjAyNSA1LjEzMiAzLjEzMS0yOS41NDIgOC42NC02Mi40NDYuNjgtNDEuMzA5LTYuMzM2eiI+PC9wYXRoPjxwYXRoIGZpbGw9IiNFQTJEMkUiIGQ9Ik02OS44MDIgNjEuMjcxYzYuMDI1IDYuOTM1LTEuNTggMTMuMTctMS41OCAxMy4xN3MxNS4yODktNy44OTEgOC4yNjktMTcuNzc3Yy02LjU1OS05LjIxNS0xMS41ODctMTMuNzkyIDE1LjYzNS0yOS41OCAwIC4wMDEtNDIuNzMxIDEwLjY3LTIyLjMyNCAzNC4xODd6Ij48L3BhdGg+PHBhdGggZmlsbD0iIzAwNzRCRCIgZD0iTTEwMi4xMjMgMTA4LjIyOXMzLjUyOSAyLjkxLTMuODg4IDUuMTU5Yy0xNC4xMDIgNC4yNzItNTguNzA2IDUuNTYtNzEuMDk0LjE3MS00LjQ1MS0xLjkzOCAzLjg5OS00LjYyNSA2LjUyNi01LjE5MiAyLjczOS0uNTkzIDQuMzAzLS40ODUgNC4zMDMtLjQ4NS00Ljk1My0zLjQ4Ny0zMi4wMTMgNi44NS0xMy43NDMgOS44MTUgNDkuODIxIDguMDc2IDkwLjgxNy0zLjYzNyA3Ny44OTYtOS40Njh6TTQ5LjkxMiA3MC4yOTRzLTIyLjY4NiA1LjM4OS04LjAzMyA3LjM0OGM2LjE4OC44MjggMTguNTE4LjYzOCAzMC4wMTEtLjMyNiA5LjM5LS43ODkgMTguODEzLTIuNDc0IDE4LjgxMy0yLjQ3NHMtMy4zMDggMS40MTktNS43MDQgMy4wNTNjLTIzLjA0MiA2LjA2MS02Ny41NDQgMy4yMzgtNTQuNzMxLTIuOTU4IDEwLjgzMi01LjIzOSAxOS42NDQtNC42NDMgMTkuNjQ0LTQuNjQzek05MC42MDkgOTMuMDQxYzIzLjQyMS0xMi4xNjcgMTIuNTkxLTIzLjg2IDUuMDMyLTIyLjI4NS0xLjg0OC4zODUtMi42NzcuNzItMi42NzcuNzJzLjY4OC0xLjA3OSAyLTEuNTQzYzE0Ljk1My01LjI1NSAyNi40NTEgMTUuNTAzLTQuODIzIDIzLjcyNSAwLS4wMDIuMzU5LS4zMjcuNDY4LS42MTd6Ij48L3BhdGg+PHBhdGggZmlsbD0iI0VBMkQyRSIgZD0iTTc2LjQ5MSAxLjU4N3MxMi45NjggMTIuOTc2LTEyLjMwMyAzMi45MjNjLTIwLjI2NiAxNi4wMDYtNC42MjEgMjUuMTMtLjAwNyAzNS41NTktMTEuODMxLTEwLjY3My0yMC41MDktMjAuMDctMTQuNjg4LTI4LjgxNSA4LjU0OC0xMi44MzQgMzIuMjI5LTE5LjA1OSAyNi45OTgtMzkuNjY3eiI+PC9wYXRoPjxwYXRoIGZpbGw9IiMwMDc0QkQiIGQ9Ik01Mi4yMTQgMTI2LjAyMWMyMi40NzYgMS40MzcgNTctLjggNTcuODE3LTExLjQzNiAwIDAtMS41NzEgNC4wMzItMTguNTc3IDcuMjMxLTE5LjE4NiAzLjYxMi00Mi44NTQgMy4xOTEtNTYuODg3Ljg3NCAwIC4wMDEgMi44NzUgMi4zODEgMTcuNjQ3IDMuMzMxeiI+PC9wYXRoPjwvc3ZnPg==
[Java-url]: https://www.java.com/
[System-Under-Test-url]: https://github.com/arpdkvcs/stackoverflow-tw
[Credential-Generator-url]: https://github.com/arpdkvcs/Credential-Generator

