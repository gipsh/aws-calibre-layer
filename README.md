# aws-calibre-layer
calibre layer for ebook format conversion as a service 
using `ebook-convert` on the cloud

## build 

clone the repo 

```
git clone https://github.com/gipsh/aws-calibre-layer
```

Run `build.sh` to build the layer, this script downloads calibre and strip stuff. 

```
chmod +x ./build.sh
./build.sh
```

## deploy 

For cloud deploy i use `serverless` aka `sls`
if you dont have it install it most of the times with `npm install -g serverless` 

Now deploy the layer

```
sls deploy
```
Thats it

## example

On `src/` you will find an example lambda function which conver from epub to mobi using the deployed layer.

The example is deployed with the layer to use it you need to publish a SNS message on the created queue. 
The message needs an atributte called `epub` with the key of a epub located on one of your S3 buckets.  










