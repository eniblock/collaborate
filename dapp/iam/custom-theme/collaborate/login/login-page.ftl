<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <div id="kc-login-page-container">
        <#if section = "title">
            ${msg("loginTitle",realm.name)}
        <#elseif section = "header">
            ${msg("loginTitleHtml",realm.name)}
        <#elseif section = "form">
            <form id="kc-totp-login-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
                <div class="${properties.kcFormGroupClass!}">
                    <div class="${properties.kcInputWrapperClass!}">
                        <div class="mdc-text-field mdc-text-field--filled ${properties.kcLabelClass!}">
                            <input id="email_code" class="mdc-text-field__input ${properties.kcInputClass!}"
                                   name="email_code" value="" type="text" autocomplete="off"
                                   placeholder="Please enter the code received by email">
                            <div class="${properties.kcLabelWrapperClass!}">
                                <label for="email_code" class="mdc-floating-label mdc-floating-label--float-above ${properties.kcLabelClass!}">
                                    Code
                                </label>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="${properties.kcFormGroupClass!}">
                    <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                        <div class="${properties.kcFormOptionsWrapperClass!}">
                        </div>
                    </div>

                    <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                        <div class="${properties.kcFormButtonsWrapperClass!}">
                            <input type="hidden" id="id-hidden-input" name="credentialId"
                                   <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>
                            <button class="mdc-button mdc-button--raised mdc-login-button ${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}"
                                    name="login" id="kc-login" type="submit">
                                ${msg("doLogIn")}
                            </button>
                        </div>
                    </div>
                </div>
            </form>
        </#if>
    </div>
</@layout.registrationLayout>
