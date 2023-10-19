//初始化常量
const CLUSTER_API = "/v1/kubernetes/clusters";
const CLUSTER_LIST_VIEWS = "/administrator/cluster?action=list";

function clusteradd() {
    let data = {};
    let value = $('#cluster-add-form').serializeArray();
    $.each(value, function (index, item) {
        data[item.name] = item.value;
    });
    // Send a POST request
    axios({
        method: 'post',
        url: CLUSTER_API,
        data: data
    }).then(function (response) {
        $('#modal-cluster-add').modal('hide');
        $('#cluster-list-fragment').load(CLUSTER_LIST_VIEWS, function () {

        });

        ok(response);
    }).catch(function (error) {
        fail(error);
    });
}

//
// function userEditSave() {
//     let data = {};
//     let value = $('#user-edit-form').serializeArray();
//     $.each(value, function (index, item) {
//         data[item.name] = item.value;
//     });
//     // Send a POST request
//     axios({
//         method: 'put',
//         url: USER_API,
//         data: data
//     }).then(function (response) {
//         $('#users-fragment').load(USER_LIST_VIEWS, function () {
//             userPaging();
//         });
//
//         ok(response);
//     }).catch(function (error) {
//         fail(error);
//     });
// }