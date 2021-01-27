"""
A `Flask <http://flask.pocoo.org/>`_ server for serving predictions
from a single AllenNLP model. It also includes a very, very bare-bones
web front-end for exploring predictions (or you can provide your own).

For example, if you have your own predictor and model in the `my_stuff` package,
and you want to use the default HTML, you could run this like

```
python -m allennlp.service.server_simple \
    --archive-path allennlp/tests/fixtures/bidaf/serialization/model.tar.gz \
    --predictor machine-comprehension \
    --title "Demo of the Machine Comprehension Text Fixture" \
    --field-name question --field-name passage
```
"""
from typing import Callable
import json
import logging
import spacy
import warnings

from flask import Flask, request, Response, jsonify, send_file, send_from_directory
from flask_cors import CORS
from gevent.pywsgi import WSGIServer

from allennlp import pretrained
from allennlp.common import JsonDict
from allennlp import predictors
from allennlp.models.archival import load_archive
from allennlp.predictors import Predictor
from pywsd.lesk import simple_lesk

logger = logging.getLogger(__name__)  # pylint: disable=invalid-name

class ServerError(Exception):
    status_code = 400

    def __init__(self, message, status_code=None, payload=None):
        Exception.__init__(self)
        self.message = message
        if status_code is not None:
            self.status_code = status_code
        self.payload = payload

    def to_dict(self):
        error_dict = dict(self.payload or ())
        error_dict['message'] = self.message
        return error_dict

class PretrainedModel:
    """
    A pretrained model is determined by both an archive file
    (representing the trained model)
    and a choice of predictor.
    """
    def __init__(self, archive_file: str, predictor_name: str) -> None:
        self.archive_file = archive_file
        self.predictor_name = predictor_name

    def predictor(self) -> Predictor:
        archive = load_archive(self.archive_file)
        return Predictor.from_archive(archive, self.predictor_name)



def make_app(predictors,
             sanitizer: Callable[[JsonDict], JsonDict] = None) -> Flask:
    """
    Creates a Flask app that serves up the provided ``Predictor``
    along with a front-end for interacting with it.

    If you want to use the built-in bare-bones HTML, you must provide the
    field names for the inputs (which will be used both as labels
    and as the keys in the JSON that gets sent to the predictor).

    If you would rather create your own HTML, call it index.html
    and provide its directory as ``static_dir``. In that case you
    don't need to supply the field names -- that information should
    be implicit in your demo site. (Probably the easiest thing to do
    is just start with the bare-bones HTML and modify it.)

    In addition, if you want somehow transform the JSON prediction
    (e.g. by removing probabilities or logits)
    you can do that by passing in a ``sanitizer`` function.
    """

    app = Flask(__name__)  # pylint: disable=invalid-name

    @app.errorhandler(ServerError)
    def handle_invalid_usage(error: ServerError) -> Response:  # pylint: disable=unused-variable
        response = jsonify(error.to_dict())
        response.status_code = error.status_code
        return response

    @app.route('/predictsrl', methods=['POST', 'OPTIONS'])
    def predictsrl() -> Response:  # pylint: disable=unused-variable
        """make a prediction using the specified model and return the results"""
        if request.method == "OPTIONS":
            return Response(response="", status=200)

        data = request.get_json()
        predictor = predictors[0]
        prediction = predictor.predict_json(data)
        if sanitizer is not None:
            prediction = sanitizer(prediction)

        log_blob = {"inputs": data, "outputs": prediction}
        logger.info("prediction: %s", json.dumps(log_blob))

        return jsonify(prediction)

    @app.route('/predictdep', methods=['POST', 'OPTIONS'])
    def predictdep() -> Response:  # pylint: disable=unused-variable
        """make a prediction using the specified model and return the results"""
        if request.method == "OPTIONS":
            return Response(response="", status=200)

        data = request.get_json()
        predictor = predictors[1]
        prediction = predictor.predict_json(data)
        if sanitizer is not None:
            prediction = sanitizer(prediction)

        log_blob = {"inputs": data, "outputs": prediction}
        logger.info("prediction: %s", json.dumps(log_blob))

        return jsonify(prediction)

    @app.route('/predictner', methods=['POST', 'OPTIONS'])
    def predictner() -> Response:  # pylint: disable=unused-variable
        """make a prediction using the specified model and return the results"""
        if request.method == "OPTIONS":
            return Response(response="", status=200)

        data = request.get_json()
        predictor = predictors[2]
        prediction = predictor.predict_json(data)
        if sanitizer is not None:
            prediction = sanitizer(prediction)

        log_blob = {"inputs": data, "outputs": prediction}
        logger.info("prediction: %s", json.dumps(log_blob))

        return jsonify(prediction)

    @app.route('/predictfinener', methods=['POST', 'OPTIONS'])
    def predictfinener() -> Response:  # pylint: disable=unused-variable
        """make a prediction using the specified model and return the results"""
        if request.method == "OPTIONS":
            return Response(response="", status=200)

        data = request.get_json()
        predictor = predictors[5]
        prediction = predictor.predict_json(data)
        if sanitizer is not None:
            prediction = sanitizer(prediction)

        log_blob = {"inputs": data, "outputs": prediction}
        logger.info("prediction: %s", json.dumps(log_blob))

        return jsonify(prediction)

    @app.route('/predictlemma', methods=['POST', 'OPTIONS'])
    def predictlemma() -> Response:  # pylint: disable=unused-variable
        """make a prediction using the specified model and return the results"""
        if request.method == "OPTIONS":
            return Response(response="", status=200)

        data = request.get_json()
        predictor = predictors[3]
        sentence = data["sentence"]
        doc = predictor(sentence)
        words = [token.text for token in doc]
        lemmas = [token.lemma_ for token in doc]
        log_blob = {"words" : words, "lemmas" : lemmas }

        return jsonify(log_blob)

    @app.route('/predictwsd', methods=['POST', 'OPTIONS'])
    def predictwsd() -> Response:  # pylint: disable=unused-variable
        """make a prediction using the specified model and return the results"""
        if request.method == "OPTIONS":
            return Response(response="", status=200)

        data = request.get_json()
        sentence = data["sentence"]
        word = data["word"]
        answer = simple_lesk(sentence, word)
        hypernym = ""
        for synset in answer.hypernyms():
            lemma_list = synset.lemmas()
            hypernym = lemma_list[0].name()
            break
        log_blob = {"hypernym": hypernym}

        return jsonify(log_blob)

    @app.route('/predictcoref', methods=['POST', 'OPTIONS'])
    def predictcoref() -> Response:  # pylint: disable=unused-variable
        """make a prediction using the specified model and return the results"""
        if request.method == "OPTIONS":
            return Response(response="", status=200)

        data = request.get_json()
        predictor = predictors[4]
        prediction = predictor.predict_json(data)

        log_blob = {"inputs": data, "outputs": prediction}
        logger.info("prediction: %s", json.dumps(log_blob))

        return jsonify(prediction)

    return app

def srl_with_elmo_luheng_2018() -> predictors.SemanticRoleLabelerPredictor:
    with warnings.catch_warnings():
        warnings.simplefilter(action="ignore", category=DeprecationWarning)
        model = PretrainedModel('https://s3-us-west-2.amazonaws.com/allennlp/models/srl-model-2018.05.25.tar.gz',
                                'semantic-role-labeling')
        return model.predictor() # type: ignore

def biaffine_parser_stanford_dependencies_todzat_2017() -> predictors.BiaffineDependencyParserPredictor:
    with warnings.catch_warnings():
        warnings.simplefilter(action="ignore", category=DeprecationWarning)
        model = PretrainedModel('https://s3-us-west-2.amazonaws.com/allennlp/models/biaffine-dependency-parser-ptb-2018.08.23.tar.gz',
                                'biaffine-dependency-parser')
        return model.predictor() # type: ignore

def named_entity_recognition_with_elmo_peters_2018() -> predictors.SentenceTaggerPredictor:
    with warnings.catch_warnings():
        warnings.simplefilter(action="ignore", category=DeprecationWarning)
        model = PretrainedModel('https://s3-us-west-2.amazonaws.com/allennlp/models/ner-model-2018.12.18.tar.gz',
                                'sentence-tagger')
        return model.predictor() # type: ignore

def fine_grained_named_entity_recognition_with_elmo_peters_2018() -> predictors.SentenceTaggerPredictor:
    model = PretrainedModel('https://s3-us-west-2.amazonaws.com/allennlp/models/fine-grained-ner-model-elmo-2018.12.21.tar.gz',
                            'sentence-tagger')
    return model.predictor() # type: ignore

def lemma():
    return spacy.load('en_core_web_sm')

def _get_predictor():

        return [pretrained.srl_with_elmo_luheng_2018(), biaffine_parser_stanford_dependencies_todzat_2017(),
                named_entity_recognition_with_elmo_peters_2018(), lemma(), pretrained.neural_coreference_resolution_lee_2017(),
                fine_grained_named_entity_recognition_with_elmo_peters_2018()]

def main():


    predictor = _get_predictor()

    app = make_app(predictor)
    CORS(app)

    http_server = WSGIServer(('0.0.0.0', 8001), app)
    print(f"Model loaded, serving demo on port {8001}")
    http_server.serve_forever()

if __name__ == "__main__":
    main()
