# markdown-to-html-server

Effective way to publish your Markdown to a web app. This tool provides option to create html from markdown and start HTTP server to serve your Markdown pages.

## Creating html pages

From markdown html pages can be created using [Documentation Builder](https://snapcraft.io/documentation-builder)

Once html pages created, our `build-html` script will run some optimization steps, to make the page optimized with common style sheets, adding SEO meta tags, or any other common tags on top of the page and get the `site-data` ready to be served as web app.

## Starting docs server

1. Download the latest [release](https://github.com/next-time-space/markdown-to-html-server/releases)
2. Delete sample markdown files from `site-data-md` and copy all your markdown files.
3. I'm using Documentation Builder to convert all markdown files to html, you can use any tool that converts to html.
4. Update the `metadata.yaml` file for navigation and other meta data. For more information check [here](https://docs.ubuntu.com/documentation-builder/en/)
5. Run the `build-html.sh` you should see a folder created `site-data` with all compiled html and static folder.
6. Update `conf.yml` file with required options. For more information on conf.yml check [here](https://github.com/next-time-space/markdown-to-html-server#configuration).
7. Start server using command, `java -jar markdown-to-html-server.jar`

## Configuration

```
http:
  # Port to start server
  port: 8083
  # Context path for serving html pages - Optional
  contextPath: /docs

resources:
  # To server static content, all the files and folders will be shared public
  static: ./site-data/static
  # Directory to specify where all html files exists and all files will be served. index.html file will be served at context path
  site-data: ./site-data
  # Files will be parsed and include in request mapping only if it matches the following extension - Optional, default: .html
  extension: .html
  # To specify if request mapping URI should have extensions or not. Ex if true, URI will be overview.html; overview otherwise.
  keep-exetension: false
```

## Customizing styles

Styles are rendered from `build/static-files/kernal.css` Styles can be overwritten using this file, running `build-html.sh` another time will make sure all styles are moved to site-data.