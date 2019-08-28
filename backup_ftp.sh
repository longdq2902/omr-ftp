#!/bin/sh

SRCDIR="/home/ftpuser/ftp/bk_upload"
DESTDIR="/home/ftpuser/ftp/bk_upload/"

PREFIX=$(date '+%m%d%Y' --date='7 days ago')
FILENAME=bk-$PREFIX.tar.gz

#echo $FILENAME

tar -zcvf $DESTDIR$FILENAME $SRCDIR/$PREFIX/
rm -rf $SRCDIR/$PREFIX




