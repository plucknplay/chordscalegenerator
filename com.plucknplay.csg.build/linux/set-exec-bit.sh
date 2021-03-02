#!/bin/sh

path=/home/plucknplay/web/pluck-n-play.com/test/data
filename_32=CSG-linux-gtk.tar.gz
filename_64=CSG-linux-gtk_64.tar.gz
csg=ChordScaleGenerator

process () {
    
    echo
    echo "Process: $1"
    echo "--------"
    
    cd $path
    
    if [ ! -f $1 ]; then 
	echo "Die Datei $1 befindet sich nicht im Ordner $path"
	return 1
    fi

    echo "untar $1"
    tar -xzf $1
    
    echo "remove $1"
    rm $1
    
    echo "set executable bit"
    chmod +x -v $csg/$csg
    
    echo "create new tar.gz-file "
    tar -czf $1 $csg/
    
    echo "remove $csg/"
    rm -R $csg/
}

process $filename_32
process $filename_64
echo

exit 0