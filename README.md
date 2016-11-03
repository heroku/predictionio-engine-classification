# [PredictionIO](http://predictionio.incubator.apache.org) classification engine for Heroku

Demo engine for the [PredictionIO Heroku buildpack](https://github.com/heroku/predictionio-buildpack). See the 📚 [README](https://github.com/heroku/predictionio-buildpack/blob/master/README.md) for how-to.

Based on the [attribute-based classifier template](https://github.com/apache/incubator-predictionio-template-attribute-based-classifier), minimally modified for machine learning on Heroku.


## [Data](data/) shape

Service plans are:

* `1` **More Voice**: expanded talk time to 1000 minutes
* `2` **More Data**: expanded transfer quota to 1000 megabytes
* `3` **More Texts**: expanded SMS to 1000 messages
* `4` **Voice + Data**: expanded talk time & transfer quota
* `5` **Data + Text**: expanded transfer quota & SMS
* `6` **Voice + Text**: expanded talk time & SMS
* `7` **Low Usage**: no services significantly utilized
