# [PredictionIO](https://predictionio.incubator.apache.org) classification engine for [Heroku](http://www.heroku.com) 

A machine learning classifier deployable to Heroku with the [PredictionIO buildpack](https://github.com/heroku/predictionio-buildpack).

[Spark's Random Forests algorithm](https://spark.apache.org/docs/1.6.2/mllib-ensembles.html) is used to predict a label using decision trees. See [A Visual Introduction to Machine Learning](http://www.r2d3.us/visual-intro-to-machine-learning-part-1/) to learn why decision trees are so effective.

Based on the [attribute-based classifier template](https://github.com/apache/incubator-predictionio-template-attribute-based-classifier) modified to use an [alternative algorithm](http://predictionio.incubator.apache.org/templates/classification/add-algorithm/). Originally this engine implemented [Spark's Naive Bayes algorithm](https://spark.apache.org/docs/1.6.2/mllib-naive-bayes.html). We soon switched to Random Forests to improve predictions by correlating attributes, a well-known weakness of Naive Bayes. The Bayes algorithm is still available in the engine source.


## Demo Story 🐸

This engine demonstrates prediction of the best fitting **service plan** for a **mobile phone user** based on their **voice, data, and text usage**. The model is trained with a small, example data set.

The **service plans** labelled in the [included training data](data/) are:

* `0` **Low Usage**: no services significantly utilized
* `1` **More Voice**: expanded talk time to 1000 minutes
* `2` **More Data**: expanded transfer quota to 1000 megabytes
* `3` **More Texts**: expanded SMS to 1000 messages
* `4` **Voice + Data**: expanded talk time & transfer quota
* `5` **Data + Text**: expanded transfer quota & SMS
* `6` **Voice + Text**: expanded talk time & SMS
* `7` **More Everything**: all services used evenly


## How To 📚

✏️ Throughout this document, code terms that start with `$` represent a value (shell variable) that should be replaced with a customized value, e.g `$EVENTSERVER_NAME`, `$ENGINE_NAME`, `$POSTGRES_ADDON_ID`…

### Deploy to Heroku

Please follow steps in order.

1. [Requirements](#requirements)
1. [Classification engine](#classification-engine)
   1. [Create the engine](#create-the-engine)
   1. [Import data](#import-data)
   1. [Deploy the engine](#deploy-the-engine)
   1. [Scale-up](#scale-up)
   1. [Retry release](#retry-release)
1. [Diagnostics](#diagnostics)
1. [Local development](#local-development)

### Usage

Once deployed, how to work with the engine.

* 🎯 [Query for predictions](#query-for-predictions)
* [Diagnostics](#diagnostics)


# Deploy to Heroku 🚀

## Requirements

* [Heroku account](https://signup.heroku.com)
* [Heroku CLI](https://toolbelt.heroku.com), command-line tools
* [git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)

## Classification Engine

### Create the engine

```bash
git clone \
  https://github.com/heroku/predictionio-engine-classification.git \
  pio-engine-classi

cd pio-engine-classi

heroku create $ENGINE_NAME
heroku buildpacks:set https://github.com/heroku/predictionio-buildpack.git
heroku addons:create heroku-postgresql:hobby-dev
```

### Import data

Initial training data is automatically imported from [`data/initial-events.json`](data/initial-events.json).

👓 When you're ready to begin working with your own data, see [data import methods in CUSTOM docs](https://github.com/heroku/predictionio-buildpack/blob/master/CUSTOM.md#import-data).

### Deploy the engine

```bash
# Wait to deploy until the database is ready
heroku pg:wait

git push heroku master

# Follow the logs to see web process start-up
#
heroku logs -t
```

⚠️ **Initial deploy will probably fail due to memory constraints.** Proceed to scale up.

## Scale up

Once deployed, scale up the processes. These are paid, [professional dyno types](https://devcenter.heroku.com/articles/dyno-types#available-dyno-types):

```bash
heroku ps:scale \
  web=1:Standard-2X \
  release=0:Performance-L \
  train=0:Performance-L
```

## Retry release

When the release (`pio train`) fails due to memory constraints or other transient error, you may use the Heroku CLI [releases:retry plugin](https://github.com/heroku/heroku-releases-retry) to rerun the release without pushing a new deployment:

```bash
# First time, install it.
heroku plugins:install heroku-releases-retry

# Re-run the release & watch the logs
heroku releases:retry
heroku logs -t
```


# Usage ⌨️

## Query for predictions

Once deployment completes, the engine is ready to predict the best fitting **service plan** for a **mobile phone user** based on their **voice, data, and text usage**.

Submit queries containing these three user attributes to get predictions using [Spark's Random Forests algorithm](https://spark.apache.org/docs/1.6.2/mllib-ensembles.html):

```bash
# Fits low usage, `0`
curl -X "POST" "https://$ENGINE_NAME.herokuapp.com/queries.json" \
     -H "Content-Type: application/json; charset=utf-8" \
     -d "{\"voice_usage\":12,\"data_usage\":0,\"text_usage\":4}"

# Fits more voice, `1`
curl -X "POST" "https://$ENGINE_NAME.herokuapp.com/queries.json" \
     -H "Content-Type: application/json; charset=utf-8" \
     -d "{\"voice_usage\":480,\"data_usage\":0,\"text_usage\":121}"

# Fits more data, `2`
curl -X "POST" "https://$ENGINE_NAME.herokuapp.com/queries.json" \
     -H "Content-Type: application/json; charset=utf-8" \
     -d "{\"voice_usage\":25,\"data_usage\":1000,\"text_usage\":80}"

#Fits more texts, `3`
curl -X "POST" "https://$ENGINE_NAME.herokuapp.com/queries.json" \
     -H "Content-Type: application/json; charset=utf-8" \
     -d "{\"voice_usage\":5,\"data_usage\":80,\"text_usage\":1000}"

#Extreme voice & data, `4`
curl -X "POST" "https://$ENGINE_NAME.herokuapp.com/queries.json" \
     -H "Content-Type: application/json; charset=utf-8" \
     -d "{\"voice_usage\":450,\"data_usage\":1104,\"text_usage\":43}"

#Extreme data & text, `5`
curl -X "POST" "https://$ENGINE_NAME.herokuapp.com/queries.json" \
     -H "Content-Type: application/json; charset=utf-8" \
     -d "{\"voice_usage\":24,\"data_usage\":770,\"text_usage\":482}"

#Extreme voice & text, `6`
curl -X "POST" "https://$ENGINE_NAME.herokuapp.com/queries.json" \
     -H "Content-Type: application/json; charset=utf-8" \
     -d "{\"voice_usage\":450,\"data_usage\":80,\"text_usage\":332}"

#Everything equal / balanced usage, `7`
curl -X "POST" "https://$ENGINE_NAME.herokuapp.com/queries.json" \
     -H "Content-Type: application/json; charset=utf-8" \
     -d "{\"voice_usage\":450,\"data_usage\":432,\"text_usage\":390}"
```

For a production model, more aspects of a user account and their correlations might be taken into consideration, including: account type (individual, business, or family), frequency of roaming, international usage, device type (smart phone or feature phone), age of device, etc.


## Diagnostics

If you hit any snags with the engine serving queries, check the logs:

```bash
heroku logs -t --app $ENGINE_NAME
```

If errors are occuring, sometimes a restart will help:

```bash
heroku restart --app $ENGINE_NAME
```

## Local Development

Deployment to Heroku is great, but if you want to customize an engine, then you'll need to get it running locally on your computer.

Use the buildpack to setup [local development](https://github.com/heroku/predictionio-buildpack/blob/master/DEV.md).
