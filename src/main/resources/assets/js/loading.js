function LoadingBox(element) {
  var template = element.html();
  var box;

  var $processBar;
  var process;
  var loop;
  var $processInc;
  var callBack;

  LoadingBox.prototype.init = function () {
    box = bootbox.dialog({
      closeButton: false,
      message: template
    });
    box.find(".modal-content").css("background","transparent");
    box.find(".modal-content").css("border","none");

    $processBar = box.find("#process");
    process = 2;
    loop = setInterval(increase, 1);
    $processInc = $("#processInc");
  }

  function increase() {
    if (process >= 100) {
      clearInterval(loop);
      box.find("img").hide();
      $processBar.hide();
      if (callBack != undefined) {
        callBack();
      }
    } else {
      var increase = $processInc.val();
      process+= parseFloat(increase);
      $processBar.css("width", process + "%");
    }
  }

  LoadingBox.prototype.success = function (onCallback) {
    $processInc.val(1);
    callBack = onCallback;
  }

  LoadingBox.prototype.close = function () {
    box.hide();
  }

  LoadingBox.prototype.isShow = function () {
      return $('.modal.in').length > 0;
  }
}