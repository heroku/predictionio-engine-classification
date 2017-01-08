# [PredictionIO](http://predictionio.incubator.apache.org) classification engine for Heroku

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/heroku/predictionio-engine-classification/tree/singularity)

Demo engine for the [PredictionIO Heroku buildpack](https://github.com/heroku/predictionio-buildpack). See the 📚 [README](https://github.com/heroku/predictionio-buildpack/blob/master/README.md) for how-to.

Based on the [attribute-based classifier template](https://github.com/apache/incubator-predictionio-template-attribute-based-classifier), modified to use an [alternative algorithm](http://predictionio.incubator.apache.org/templates/classification/add-algorithm/), [Random Forests](https://en.wikipedia.org/wiki/Random_forest).

## [Data](data/) shape

Service plans are:

* `0` **Low Usage**: no services significantly utilized
* `1` **More Voice**: expanded talk time to 1000 minutes
* `2` **More Data**: expanded transfer quota to 1000 megabytes
* `3` **More Texts**: expanded SMS to 1000 messages
* `4` **Voice + Data**: expanded talk time & transfer quota
* `5` **Data + Text**: expanded transfer quota & SMS
* `6` **Voice + Text**: expanded talk time & SMS
* `7` **More Everything**: all services used evenly

## Deploy from Source

*This is the work-around for button deploy not being able to size release process on first run.*

```bash
git clone \
  https://github.com/heroku/predictionio-engine-classification.git \
  pio-engine-classification

cd pio-engine-classification

git checkout singularity
export APP_NAME=pio-singularity

heroku create $APP_NAME
heroku addons:create heroku-postgresql:hobby-dev -a $APP_NAME

# Set config vars like app.json/Button Deploy would.
heroku config:set PIO_EVENTSERVER_ACCESS_KEY=`ruby -r securerandom -e 'STDOUT << SecureRandom.hex(32)'`  -a $APP_NAME
heroku config:set PREDICTIONIO_DIST_URL=https://marsikai.s3.amazonaws.com/PredictionIO-0.11.0-alpha-stateless.tar.gz -a $APP_NAME

heroku buildpacks:add -i 1 https://github.com/heroku/heroku-buildpack-addon-wait.git -a $APP_NAME
heroku buildpacks:add -i 2 https://github.com/heroku/heroku-buildpack-space-proxy.git -a $APP_NAME
heroku buildpacks:add -i 3 https://github.com/heroku/heroku-buildpack-jvm-common.git -a $APP_NAME
heroku buildpacks:add -i 4 https://github.com/heroku/predictionio-buildpack.git -a $APP_NAME
heroku buildpacks:add -i 5 https://github.com/kr/heroku-buildpack-inline.git -a $APP_NAME
heroku buildpacks:add -i 6 https://github.com/codeship/heroku-buildpack-runit.git -a $APP_NAME

git push heroku singularity:master

# Ideally this would happen before first deploy
heroku ps:scale \
  web=1:Standard-2X \
  release=0:Performance-L \
  train=0:Performance-L \
  -a $APP_NAME

# Kludge to get the release run with enough memory,
# on a Performance-L dyno
heroku plugins:install heroku-releases-retry
heroku releases:retry --app $APP_NAME
```