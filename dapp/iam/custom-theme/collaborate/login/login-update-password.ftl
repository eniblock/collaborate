<#import "login-update-password-layout.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "title">
        ${msg("updatePasswordTitle")?no_esc}
    <#elseif section = "header">
        ${msg("updatePasswordTitle")?no_esc}
    <#elseif section = "form">
        <div class="alert alert-warning">
            <span class="pficon pficon-warning-triangle-o"></span>
            <span class="kc-feedback-text">
                ${msg("updatePasswordWarning")}
            </span>
        </div>
        <div id="kc-passwd-update-username">${username}</div>
        <form id="kc-passwd-update-form" class="form update-password ${properties.kcFormClass!}"
              action="${url.loginAction}" method="post">
            <input type="text" readonly value="this is not a login form" style="display: none;">
            <input type="password" readonly value="this is not a login form" style="display: none;">

            <div class="update-password-field ${properties.kcFormGroupClass!}">
                <div class="${properties.kcInputWrapperClass!}">
                    <div class="mdc-text-field mdc-text-field--filled ${properties.kcLabelClass!} <#if usernameEditDisabled??>mdc-text-field--disabled</#if>">
                        <input id="password-new" class="mdc-text-field__input ${properties.kcInputClass!}"
                               name="password-new" value="" type="password"
                               autocomplete="off">
                        <i onclick="show('password-new')"
                           class="eye-icon material-icons mdc-text-field__icon mdc-text-field__icon--trailing"
                           tabindex="0" role="button">remove_red_eye</i>
                        <div class="${properties.kcLabelWrapperClass!}">
                            <label for="password-new" class="mdc-floating-label mdc-floating-label--float-above ${properties.kcLabelClass!}">
                                ${msg("passwordNew")}
                            </label>
                        </div>
                    </div>
                </div>
            </div>

            <div class="update-password-field ${properties.kcFormGroupClass!}">
                <div class="${properties.kcInputWrapperClass!}">
                    <div class="mdc-text-field mdc-text-field--filled ${properties.kcLabelClass!} <#if usernameEditDisabled??>mdc-text-field--disabled</#if>">
                        <input id="password-confirm" class="mdc-text-field__input ${properties.kcInputClass!}"
                               name="password-confirm" value="" type="password"
                               autocomplete="off">
                        <i onclick="show('password-confirm')"
                           class="eye-icon material-icons mdc-text-field__icon mdc-text-field__icon--trailing"
                           tabindex="0" role="button">remove_red_eye</i>
                        <div class="${properties.kcLabelWrapperClass!}">
                            <label for="password-confirm" class="mdc-floating-label ${properties.kcLabelClass!}">
                                ${msg("passwordConfirm")}
                            </label>
                        </div>
                    </div>
                </div>
            </div>

            <div class="${properties.kcFormGroupClass!} row update-password-button-container">
                <div id="kc-form-options" class="${properties.kcFormOptionsClass!} col-xs-6 col-sm-8">
                    <div class="${properties.kcFormOptionsWrapperClass!}">
                    </div>
                </div>

                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!} col-xs-6 col-sm-4">
                    <button class="mdc-button mdc-button--raised mdc-login-button ${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}"
                            type="submit">
                        ${msg("doSubmit")}
                    </button>
                </div>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>
