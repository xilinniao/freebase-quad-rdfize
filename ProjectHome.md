Parses every line in quad dump and converts it to RDF triples. Currently does not do a MQL query to fetch freebase schema before conversion. Plans to improve it down the line.

As of now it just checks whether quad data falls into 3 categories:
1. Data with only subject, property and to.
2. Data with subject, property and value.
3. Data with subject, property, both to and value.
> - Text data
> - Key data