'use strict';

var commentFormHandler = {};

function CommentFormHandler() {
  var cfh = this;
  
  this.init = function() {
    this.commentsApiUrl = '../api/comments';
    this.authorNicknamePattern = /^[a-zA-Z0-9]+$/;
    var formElementIds = { form: 'newCommentForm'
                       , postIdInput: 'postIdInput'
                       , commentIdInput: 'commentIdInput'
                       , submitButton: 'submitCommentButton'
                       , cancelButton: 'cancelCommentButton'
                       , closeButton: 'closeCommentFormButton'
                       , headerDiv: 'commentFormHeaderText'
                       , clientErrorDiv: 'commentFormClientError'
                       , serverErrorDiv: 'commentFormServerError'
                       , authorNicknameInput: 'authorNicknameInput'
                       , authorNicknameError: 'authorNicknameError'
                       , contentInput: 'contentInput'
                       , contentError: 'contentError'
                       };
    for (var element in formElementIds) {
      this[element] = document.getElementById(formElementIds[element]);
    }
    this.attachToForm();
    this.attachToReplies();
  };
  
  this.disableButtons = function() {
    this.submitButton.disabled = true;
    this.cancelButton.disabled = true;
    this.closeButton.disabled = true;
  };

  this.enableButtons = function() {
    this.submitButton.disabled = false;
    this.cancelButton.disabled = false;
    this.closeButton.disabled = false;
  };

  this.showError = function(clientMessage, statusCode, serverResponse) {
	this.clientErrorDiv.innerHTML = clientMessage;
    if (statusCode == AjaxUtil.httpCode.BAD_REQUEST) {
	    var serverErrorsObject = JSON.parse(serverResponse);
	    var serverErrorsUnrolled = '';
	    for (var i = 0; i < serverErrorsObject.length; i++) {
        serverErrorsUnrolled += '<p>' + serverErrorsObject[i] + '</p>';
	    }
	    this.serverErrorDiv.innerHTML = serverErrorsUnrolled;
    } else {
	    this.serverErrorDiv.innerHTML = serverMessage;
    }
  };

  this.resetError = function() {
    this.clientErrorDiv.innerHTML = '';
    this.serverErrorDiv.innerHTML = '';
    this.authorNicknameError.className = 'hidden';
    this.contentError.className = 'hidden';
  };

  this.insertFormAfter = function(domNode) {
    var commentsBlock = document.getElementById('commentsBlock');
    if (domNode) {
	    commentsBlock.insertBefore(this.form, domNode.nextSibling);
	    this.form.style.marginLeft = parseFloat(domNode.style.marginLeft) + 1.5 + 'em';
    } else {
	    commentsBlock.insertBefore(this.form, null);
	    this.form.style.marginLeft = 0;
    }
  };

  this.submit = function(event) {
    cfh.resetError();
    if (!cfh.checkAuthorNickname() | !cfh.checkContent()) {
	    return;
    }
    cfh.disableButtons();
    var serializedForm = AjaxUtil.serializeForm(commentFormHandler.form);
    var xhr = AjaxUtil.createXhr();
    xhr.onreadystatechange = function() {
	    if (xhr.readyState == AjaxUtil.xhrState.COMPLETE) {
        if (xhr.status == AjaxUtil.httpCode.OK) {
          var newCommentId = 'comment-' + xhr.responseText;
          cfh.updateAndShowComments(newCommentId);
        } else {
          cfh.showError('Failed to post the comment', xhr.status, xhr.responseText);
        }
        cfh.enableButtons();
	    }
    };
    xhr.open('post', cfh.commentsApiUrl, true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.send(serializedForm);
  };

  this.updateAndShowComments = function(commentDivId) {
    var xhr = AjaxUtil.createXhr();
    xhr.onreadystatechange = function() {
	    if (xhr.readyState == AjaxUtil.xhrState.COMPLETE) {
        if (xhr.status == AjaxUtil.httpCode.OK) {
          var commentsBlock = document.getElementById('commentsBlock');
          commentsBlock.outerHTML = xhr.responseText;
          window.location.hash = '#' + commentDivId;
          cfh.init();
        } else {
          cfh.showError('Failed to update comments view', xhr.status, xhr.responseText);
        }
	    }
    };
    xhr.open('get', cfh.commentsApiUrl + '/' + cfh.postIdInput.value, true);
    xhr.send(null);
  };

  this.attachToReplies = function() {
    var replyButtons = document.getElementsByClassName('replyToCommentButton');
    for (var i = 0; i < replyButtons.length; i++) {
	    EventUtil.addHandler(replyButtons[i], 'click', this.moveFormToComment);
	    replyButtons[i].disabled = false;
    }
  };

  this.moveFormToComment = function(event) {
    cfh.resetError();
    cfh.form.reset();
    var commentButton = EventUtil.getTarget(event);
    var buttonId = commentButton.id;
    var commentId = buttonId.split('-')[1];
    var commentDivId = 'comment-' + commentId;
    var commentDiv = document.getElementById(commentDivId);
    cfh.insertFormAfter(commentDiv);
    cfh.commentIdInput.value = commentId;
    cfh.headerDiv.innerHTML = 'Reply';
  };

  this.moveFormToPost = function(event) {
    cfh.resetError();
    cfh.form.reset();
    cfh.insertFormAfter(null);
    cfh.commentIdInput.value = '';
    cfh.headerDiv.innerHTML = 'Comment';
  };

  this.attachToForm = function() {
    EventUtil.addHandler(this.submitButton, 'click', this.submit);
    EventUtil.addHandler(this.cancelButton, 'click', this.moveFormToPost);
    EventUtil.addHandler(this.closeButton, 'click', this.moveFormToPost);
    this.enableButtons();
  };

  this.checkAuthorNickname = function() {
    if (!this.authorNicknameInput.value) {
	    this.authorNicknameError.children[1].innerHTML = 'Nickname should not be empty';
	    this.authorNicknameError.className = 'errorRow';
	    return false;
    }
    if (!this.authorNicknamePattern.test(this.authorNicknameInput.value)) {
	    this.authorNicknameError.children[1].innerHTML =
        'Nickname should contain only latin letters (a-z, A-Z) and digits (0-9)';
	    this.authorNicknameError.className = 'errorRow';
	    return false;
    }
    return true;
  };

  this.checkContent = function() {
    if (!this.contentInput.value) {
	    this.contentError.className = 'fieldErrorMessage';
	    return false;
    }
    return true;
  };
  
}

EventUtil.addHandler(window, 'load', function(event) {
    commentFormHandler = new CommentFormHandler();
    commentFormHandler.init();
});
