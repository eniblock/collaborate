<#import "login-update-password-layout.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "header">
        ${msg("updatePasswordTitle")}
    <#elseif section = "form">
        <div class="alert alert-warning">
            <span class="pficon pficon-warning-triangle-o"></span>
            <span class="kc-feedback-text">
                ${properties.kcUpdatePasswordWarning}
            </span>
        </div>
        <div id="kc-passwd-update-username">${username}</div>
        <form id="kc-passwd-update-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
            <input type="text" id="username" name="username" value="${username}" autocomplete="username"
                   readonly="readonly" style="display:none;"/>
            <input type="password" id="password" name="password" autocomplete="current-password" style="display:none;"/>
            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="password-new" class="${properties.kcLabelClass!}">${msg("passwordNew")}</label>
                </div>
                <div class="${properties.kcInputWrapperClass!} password-container">
                    <input type="password" id="password-new" name="password-new" class="${properties.kcInputClass!}"
                           autofocus autocomplete="new-password"/>
                    <span onclick="show('password-new', 'update-eye-icon', 'update-eye-icon')"
                          class="fa fa-fw fa-eye-slash field-icon update-eye-icon eye-icon" id="update-eye-icon"></span>

                </div>
            </div>

            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="password-confirm" class="${properties.kcLabelClass!}">${msg("passwordConfirm")}</label>
                </div>
                <div class="${properties.kcInputWrapperClass!} password-container">
                    <input type="password" id="password-confirm" name="password-confirm"
                           class="${properties.kcInputClass!}" autocomplete="new-password"/>
                    <span onclick="show('password-confirm', 'update-eye-icon2', 'update-eye-icon')"
                          class="fa fa-fw fa-eye-slash field-icon update-eye-icon eye-icon"
                          id="update-eye-icon2"></span>
                </div>
            </div>

            <div class="${properties.kcFormGroupClass!}">
                <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                    <div class="${properties.kcFormOptionsWrapperClass!}">
                    </div>
                </div>

                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                    <#if isAppInitiatedAction??>
                        <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}"
                               type="submit" value="${msg("doSubmit")}"/>
                        <button
                        class="${properties.kcButtonClass!} ${properties.kcButtonDefaultClass!} ${properties.kcButtonLargeClass!}"
                        type="submit" name="cancel-aia" value="true" />${msg("doCancel")}</button>
                    <#else>
                        <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                               type="submit" value="${msg("doSubmit")}"/>
                    </#if>
                </div>
            </div>
            <script src="${url.resourcesPath}/js/script.js" type="text/javascript"></script>
        </form>
    </#if>
</@layout.registrationLayout>
