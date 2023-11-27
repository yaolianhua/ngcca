//codemirror
let buildtemplateviews_codemirror_dockerfile;
let buildtemplateviews_codemirror_yaml;

$(function () {
    buildtemplateviews_codemirror_dockerfile = CodeMirror.fromTextArea(document.getElementById("buildtemplateviews-codemirror-dockerfile"), {
        mode: "dockerfile",
        theme: "monokai",
        lineNumbers: true,
        readOnly: true
    });

    buildtemplateviews_codemirror_yaml = CodeMirror.fromTextArea(document.getElementById("buildtemplateviews-codemirror-yaml"), {
        mode: "yaml",
        theme: "monokai",
        lineNumbers: true,
        readOnly: true
    });

});

function sourcecode() {
    axios.get(BUILD_TEMPLATEVIEWS_API + "/yaml/sourcecode")
        .then(response => {
            $('#build-templateviews-dockerfile').css("display", "none");
            $('#build-templateviews-yaml').css("display", "block");
            buildtemplateviews_codemirror_yaml.setValue(response.data);
            buildtemplateviews_codemirror_yaml.refresh();
        })
        .catch(error => {
            fail(error);
        });
}

function artifact() {
    axios.get(BUILD_TEMPLATEVIEWS_API + "/yaml/artifact")
        .then(response => {
            $('#build-templateviews-dockerfile').css("display", "none");
            $('#build-templateviews-yaml').css("display", "block");
            buildtemplateviews_codemirror_yaml.setValue(response.data);
            buildtemplateviews_codemirror_yaml.refresh();
        })
        .catch(error => {
            fail(error);
        });
}

function secret() {
    axios.get(BUILD_TEMPLATEVIEWS_API + "/yaml/secret")
        .then(response => {
            $('#build-templateviews-dockerfile').css("display", "none");
            $('#build-templateviews-yaml').css("display", "block");
            buildtemplateviews_codemirror_yaml.setValue(response.data);
            buildtemplateviews_codemirror_yaml.refresh();
        })
        .catch(error => {
            fail(error);
        });
}

function jar() {
    axios.get(BUILD_TEMPLATEVIEWS_API + "/dockerfile/jar")
        .then(response => {
            $('#build-templateviews-yaml').css("display", "none");
            $('#build-templateviews-dockerfile').css("display", "block");
            buildtemplateviews_codemirror_dockerfile.setValue(response.data);
            buildtemplateviews_codemirror_dockerfile.refresh();
        })
        .catch(error => {
            fail(error);
        });
}

function war() {
    axios.get(BUILD_TEMPLATEVIEWS_API + "/dockerfile/war")
        .then(response => {
            $('#build-templateviews-yaml').css("display", "none");
            $('#build-templateviews-dockerfile').css("display", "block");
            buildtemplateviews_codemirror_dockerfile.setValue(response.data);
            buildtemplateviews_codemirror_dockerfile.refresh();
        })
        .catch(error => {
            fail(error);
        });
}

function maven() {
    axios.get(BUILD_TEMPLATEVIEWS_API + "/dockerfile/maven")
        .then(response => {
            $('#build-templateviews-yaml').css("display", "none");
            $('#build-templateviews-dockerfile').css("display", "block");
            buildtemplateviews_codemirror_dockerfile.setValue(response.data);
            buildtemplateviews_codemirror_dockerfile.refresh();
        })
        .catch(error => {
            fail(error);
        });
}


function java8runtime() {
    axios.get(BUILD_TEMPLATEVIEWS_API + "/dockerfile/java8runtime")
        .then(response => {
            $('#build-templateviews-yaml').css("display", "none");
            $('#build-templateviews-dockerfile').css("display", "block");
            buildtemplateviews_codemirror_dockerfile.setValue(response.data);
            buildtemplateviews_codemirror_dockerfile.refresh();
        })
        .catch(error => {
            fail(error);
        });
}

function java11runtime() {
    axios.get(BUILD_TEMPLATEVIEWS_API + "/dockerfile/java11runtime")
        .then(response => {
            $('#build-templateviews-yaml').css("display", "none");
            $('#build-templateviews-dockerfile').css("display", "block");
            buildtemplateviews_codemirror_dockerfile.setValue(response.data);
            buildtemplateviews_codemirror_dockerfile.refresh();
        })
        .catch(error => {
            fail(error);
        });
}

function java17runtime() {
    axios.get(BUILD_TEMPLATEVIEWS_API + "/dockerfile/java17runtime")
        .then(response => {
            $('#build-templateviews-yaml').css("display", "none");
            $('#build-templateviews-dockerfile').css("display", "block");
            buildtemplateviews_codemirror_dockerfile.setValue(response.data);
            buildtemplateviews_codemirror_dockerfile.refresh();
        })
        .catch(error => {
            fail(error);
        });
}


