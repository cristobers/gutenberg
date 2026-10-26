# Building

`mvn clean compile assembly:single`

# Running

java -jar target/static-site-generator-1.0-SNAPSHOT-jar-with-dependencies.jar --template <TEMPLATE_HTML_FILE_PATH> --markdown <MARKDOWN_MD_FILE_PATH> --o <OUTPUT_FILE_PATH>