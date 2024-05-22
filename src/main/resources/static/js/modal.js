function showModalDialog(title, message) {
  $("#modalTitle").text(title);
  $("#modalBody").text(message);
  $("#modalDialog").modal();

}

function showErrorModal(message) {
  showModalDialog("Error", message);
}
function showWarningModal(message) {
  showModalDialog("Warning", message);
}

$('#modalDialog').on('show.bs.modal', function (e) {
  var modal = $(this);
  modal.find('.modal-dialog').css({
      transform: 'translate(0, -25%)',
      transition: 'transform 0.3s ease-out'
  });
});

$('#modalDialog').on('shown.bs.modal', function (e) {
  var modal = $(this);
  modal.find('.modal-dialog').css({
      transform: 'translate(0, 0)'
  });
});