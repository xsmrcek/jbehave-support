Narrative:
In order to explain how to download file
As a development team
I want to show you how to download file and extract it's text into the context

Scenario: Download file

Given authentication is prepared for user [sa] with a password [sa]

Then file with the [pdf] from url [http://localhost:11110/download/file?name=SimplePdfFile.pdf] is downloaded, and its path is saved to context as [SIMPLE_PDF_FILE]
