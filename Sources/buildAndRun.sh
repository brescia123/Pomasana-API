#!/bin/bash
mvn clean install
cd pomasana-ear
mvn appengine:devserver