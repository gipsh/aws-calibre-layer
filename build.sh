#!/bin/bash
CALIBRE_VERSION=calibre-4.15.0-x86_64.txz
mkdir -p layer
cd layer
rm -rf *
wget https://github.com/kovidgoyal/calibre/releases/download/v4.15.0/$CALIBRE_VERSION
tar -xf $CALIBRE_VERSION 
rm $CALIBRE_VERSION

# remove some files to hit the 250MB layer limit 
rm lib/libQt5WebEngineCore.so.5
rm lib/python2.7/site-packages/calibre/gui2/viewer/highlights.pyo
rm resources/builtin_recipes.zip
rm resources/content-server/index-generated.html
rm resources/localization/locales.zip

