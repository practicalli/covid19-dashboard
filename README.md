# covid19-dashboard

A dashboard created with [Oz](https://github.com/metasoarous/oz) as an example of data science visualization.

## Installation

Download from https://github.com/practicalli/covid19-dashboard.

## Usage
Open the `src/practicalli/design_journal.clj` file in a Clojure aware editor and start a Clojure REPL.

Evaluate the whole namespace to ensure all data generators and views are loaded into the REPL.  A browser window will open and may display a series of different visualizations in quick succession.

Review the code and evaluate the `(oz/view!)` expressions to see individual visualizations.


## General actions

Run the project directly:

    $ clojure -m practicalli.covid19-dashboard

Run the project's tests (they'll fail until you edit them):

    $ clojure -A:test:runner

Build an uberjar:

    $ clojure -A:uberjar

Run that uberjar:

    $ java -jar covid19-dashboard.jar


## License

Copyright © 2020 Practicalli

Distributed under the Creative Commons Attribution Share-Alike 4.0 International
