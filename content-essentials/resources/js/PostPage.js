'use strict';
//Utility objects
var EventUtil = { addClickHandler: function(element, handler) {
                    element.addEventListener('click', handler, false);
                  }
                , addWindowLoadHandler: function(handler) {
                    window.addEventListener('load', handler, false);
                  }
                };

var AjaxUtil = { xhrState: { UNINITIALIZED: '0'
                           , OPEN: '1'
                           , SENT: '2'
                           , RECEIVING: '3'
                           , COMPLETE: '4'
                           }
               , httpCode: { OK: '200'
                           , BAD_REQUEST: '400'
                           }
  
               , createXhr: function() {
                   return new XMLHttpRequest();
                 }
               };
               
//Comment form handler related code
var commentFormHandler = {};

function CommentFormHandler() {
  var cfh = this;
  
  this.init = function() {
    this.commentsApiUrl = '../api/comments';
    this.authorNicknamePattern = /^[a-zA-Z0-9]+$/;
    var formElementIds = { form: 'newCommentForm'
                         , postIdInput: 'postIdInput'
                         , commentIdInput: 'commentIdInput'
                         , authorNicknameInput: 'authorNicknameInput'
                         , captchaInput: 'captchaInput'
                         , contentInput: 'contentInput'
                         , submitButton: 'submitCommentButton'
                         , cancelButton: 'cancelCommentButton'
                         , closeButton: 'closeCommentFormButton'
                         , headerDiv: 'commentFormHeaderText'
                         , clientErrorDiv: 'commentFormClientError'
                         , serverErrorDiv: 'commentFormServerError'
                         , authorNicknameError: 'authorNicknameError'
                         , contentError: 'contentError'
                         };
    this.formElements = {};
    for (var element in formElementIds) {
      this.formElements[element] = document.getElementById(formElementIds[element]);
    }
    this.attachToForm();
    this.attachToReplies();
  };
  
  this.disableButtons = function() {
    this.formElements.submitButton.disabled = true;
    this.formElements.cancelButton.disabled = true;
    this.formElements.closeButton.disabled = true;
  };

  this.enableButtons = function() {
    this.formElements.submitButton.disabled = false;
    this.formElements.cancelButton.disabled = false;
    this.formElements.closeButton.disabled = false;
  };

  this.showError = function(clientMessage, statusCode, serverResponse) {
	this.formElements.clientErrorDiv.innerHTML = clientMessage;
    if (statusCode == AjaxUtil.httpCode.BAD_REQUEST) {
	    var serverErrorsObject = JSON.parse(serverResponse);
	    var serverErrorsUnrolled = '';
	    for (var i = 0; i < serverErrorsObject.length; i++) {
        serverErrorsUnrolled += '<p>' + serverErrorsObject[i] + '</p>';
	    }
	    this.formElements.serverErrorDiv.innerHTML = serverErrorsUnrolled;
    } else {
	    this.formElements.serverErrorDiv.innerHTML = serverMessage;
    }
  };

  this.resetError = function() {
    this.formElements.clientErrorDiv.innerHTML = '';
    this.formElements.serverErrorDiv.innerHTML = '';
    this.formElements.authorNicknameError.className = 'hidden';
    this.formElements.contentError.className = 'hidden';
  };

  this.insertFormAfter = function(domNode) {
    var commentsBlock = document.getElementById('commentsBlock');
    if (domNode) {
	    commentsBlock.insertBefore(this.formElements.form, domNode.nextSibling);
	    this.formElements.form.style.marginLeft = parseFloat(domNode.style.marginLeft) + 1.5 + 'em';
    } else {
	    commentsBlock.insertBefore(this.formElements.form, null);
	    this.formElements.form.style.marginLeft = 0;
    }
  };
  
  this.serializeCommentForm = function() {
    var parts = [];
    var field = null;
    for (var formElement in this.formElements) {
      field = this.formElements[formElement];
      if (field.tagName === 'input' || field.tagName === 'textarea') {
        parts.push(encodeURIComponent(field.name) + '=' + encodeURIComponent(field.value));
      }
    }
    return parts.join('&');
  };

  this.submit = function(event) {
    event.preventDefault();
    cfh.resetError();
    if (!cfh.checkAuthorNickname() | !cfh.checkContent()) {
	    return;
    }
    cfh.disableButtons();
    var serializedForm = cfh.serializeCommentForm();
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
    xhr.open('get', cfh.commentsApiUrl + '/' + cfh.formElements.postIdInput.value, true);
    xhr.send(null);
  };

  this.attachToReplies = function() {
    var replyButtons = document.getElementsByClassName('replyToCommentButton');
    for (var i = 0; i < replyButtons.length; i++) {
	    EventUtil.addClickHandler(replyButtons[i], this.moveFormToComment);
	    replyButtons[i].disabled = false;
    }
  };

  this.moveFormToComment = function(event) {
    cfh.resetError();
    cfh.formElements.form.reset();
    var commentButton = event.target;
    var buttonId = commentButton.id;
    var commentId = buttonId.split('-')[1];
    var commentDivId = 'comment-' + commentId;
    var commentDiv = document.getElementById(commentDivId);
    cfh.insertFormAfter(commentDiv);
    cfh.formElements.commentIdInput.value = commentId;
    cfh.formElements.headerDiv.innerHTML = 'Reply';
  };

  this.moveFormToPost = function(event) {
    cfh.resetError();
    cfh.formElements.form.reset();
    cfh.insertFormAfter(null);
    cfh.formElements.commentIdInput.value = '';
    cfh.formElements.headerDiv.innerHTML = 'Comment';
  };

  this.attachToForm = function() {
    EventUtil.addClickHandler(this.formElements.submitButton, this.submit);
    EventUtil.addClickHandler(this.formElements.cancelButton, this.moveFormToPost);
    EventUtil.addClickHandler(this.formElements.closeButton, this.moveFormToPost);
    this.enableButtons();
  };

  this.checkAuthorNickname = function() {
    if (!this.formElements.authorNicknameInput.value) {
	    this.formElements.authorNicknameError.children[1].innerHTML = 'Nickname should not be empty';
	    this.formElements.authorNicknameError.className = 'errorRow';
	    return false;
    }
    if (!this.authorNicknamePattern.test(this.formElements.authorNicknameInput.value)) {
	    this.formElements.authorNicknameError.children[1].innerHTML =
        'Nickname should contain only latin letters (a-z, A-Z) and digits (0-9)';
	    this.formElements.authorNicknameError.className = 'errorRow';
	    return false;
    }
    return true;
  };

  this.checkContent = function() {
    if (!this.formElements.contentInput.value) {
	    this.formElements.contentError.className = 'fieldErrorMessage';
	    return false;
    }
    return true;
  };
  
}

//Initialization of the form handler
EventUtil.addWindowLoadHandler(function(event) {
    commentFormHandler = new CommentFormHandler();
    commentFormHandler.init();
});
