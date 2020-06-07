#!/bin/bash
mkdir -p layer
cd layer
rm -rf *

# will download the lastest release
curl -s https://api.github.com/repos/kovidgoyal/calibre/releases/latest \
| grep "browser_download_url.*64.txz" \
| cut -d : -f 2,3 \
| tr -d \" \
| wget -qi -

tar -xf calibre-*.txz
rm calibre-*.txz

# remove some files to hit the 250MB layer limit 
rm lib/libQt5WebEngineCore.so.5
rm lib/python2.7/site-packages/calibre/gui2/viewer/highlights.pyo
rm resources/builtin_recipes.zip
rm resources/content-server/index-generated.html
rm resources/localization/locales.zip

