#Clean output if you want
rm -rf site-data
mkdir -p site-data
mkdir -p site-data/static
cp -r static-files/. site-data/static
documentation-builder --force --no-link-extensions --output-path site-data --source-folder site-data-md
seoContent=`cat meta-tags.xml`
seoContent=$(sed 's/$/\\n/' meta-tags.xml | tr -d '\n')
seoContent=${seoContent//\"/\\\"} 
seoContent=${seoContent//\//\\/}

updateScript(){
	context=''
	if [ "$(echo "$2" | cut -c 1-1)" = '/' ]
	then
		context='\'$2
	fi
    for file in "$1"/* 
    do 
    if [ -d "$file" ]
    then 
        updateScript "$file" "$2"
    else
        if [[ $file == *".html" ]];
        then
            echo 'Processing: ' $file
            sed -i -e "s/<head>/<head>$seoContent/" $file
        	sed -i '/<style>/ {:a; $!N; s/<style>.*<\/style>/<link rel="stylesheet" href="'$context'\/kernal.css"\/>/; t; ba}' $file
        fi
    fi
    done
}
updateScript "site-data" "$1"


