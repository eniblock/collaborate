<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=social.displayInfo; section>

  <div id="kc-form-login-container">
    <h2 id="kc-form-login-title">${msg("loginTitle")}</h2>
    <#if section = "form">
      <#if realm.password>
        <form id="kc-form-login" class="form ${properties.kcFormClass!}" action="${url.loginAction}"
              method="post">
          <div class="username-container ${properties.kcFormGroupClass!}">
            <div class="${properties.kcInputWrapperClass!}">
              <div class="mdc-text-field mdc-text-field--filled ${properties.kcLabelClass!} <#if usernameEditDisabled??>mdc-text-field--disabled</#if>">
                <input required id="username" class="mdc-text-field__input ${properties.kcInputClass!}"
                       name="username" value="${(login.username!'')}" type="text"
                       <#if usernameEditDisabled??>disabled</#if>>
                <div class="${properties.kcLabelWrapperClass!}">
                  <label for="username"
                         class="mdc-floating-label ${properties.kcLabelClass!}">
                    <#if !realm.loginWithEmailAllowed>
                      ${msg("username")}
                    <#elseif !realm.registrationEmailAsUsername>
                      ${msg("usernameOrEmail")}
                    <#else>
                      ${msg("email")}
                    </#if>
                  </label>
                </div>
              </div>
            </div>
          </div>
          <div class="password-container ${properties.kcFormGroupClass!}">
            <div class="${properties.kcInputWrapperClass!}">

              <div class="mdc-text-field mdc-text-field--filled ${properties.kcLabelClass!}">
                <input required id="password" class="mdc-text-field__input ${properties.kcInputClass!}"
                       name="password" type="password">
                <i onclick="show('password')"
                   class="eye-icon material-icons mdc-text-field__icon mdc-text-field__icon--trailing"
                   tabindex="0" role="button">remove_red_eye</i>
                <div class="${properties.kcLabelWrapperClass!}">
                  <label for="password"
                         class="mdc-floating-label ${properties.kcLabelClass!}">${msg("password")}</label>
                </div>
              </div>
            </div>
            <#--  <div id="capsLockWarning" style="font-weight: bold; color: maroon; margin: 0 0 10px 0; display: none;">
              <i class="fa fa-exclamation-triangle" style="color: #f0ad4e"></i>
              ${msg("capsLockWarning")}
            </div>  -->
          </div>

          <a id="forgot-password-link" href="#" onclick="goToForgotPassword();return false;">Problems with login ?</a>

          <div class="${properties.kcFormGroupClass!}">
            <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
              <div class="col-xs-7">
                <#if realm.rememberMe && !usernameEditDisabled??>

                  <div class="mdc-form-field remember-me-checkbox">
                    <div class="mdc-checkbox">
                      <input type="checkbox"
                             name="rememberMe"
                             class="mdc-checkbox__native-control"
                             id="rememberMe"
                             <#if login.rememberMe??>checked</#if>
                      />
                      <div class="mdc-checkbox__background">
                        <svg class="mdc-checkbox__checkmark"
                             viewBox="0 0 24 24">
                          <path class="mdc-checkbox__checkmark-path"
                                fill="none"
                                stroke="white"
                                d="M1.73,12.91 8.1,19.28 22.79,4.59"/>
                        </svg>
                        <div class="mdc-checkbox__mixedmark"></div>
                      </div>
                    </div>
                    <label for="rememberMe">${msg("rememberMe")}</label>
                  </div>
                </#if>
              </div>
              <div id="kc-form-buttons" class="col-xs-5 ${properties.kcFormButtonsClass!}">

                <button class="mdc-button mdc-button--raised mdc-login-button ${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}"
                        name="login" id="kc-login" type="submit">
                  ${msg("doLogIn")}
                </button>



                <div class="clearfix"></div>
              </div>
            </div>
            <#if (realm.password && realm.registrationAllowed && !usernameEditDisabled??) || realm.resetPasswordAllowed>
              <div>
                <div class="col-xs-12">
                  <hr class="separator"/>
                </div>
              </div>
              <div>
                <#if realm.password && realm.registrationAllowed && !usernameEditDisabled??>
                  <div id="kc-registration" class="col-xs-12"
                       <#if realm.password && realm.registrationAllowed && !usernameEditDisabled?? && realm.resetPasswordAllowed>style="margin-bottom: 15px;"</#if>>
                                        <span>${msg("noAccount")} <a
                                                  href="${url.registrationUrl}">${msg("doRegister")}</a></span>
                  </div>
                </#if>
              </div>
              <div>
                <div class="${properties.kcFormOptionsWrapperClass!} col-xs-12">
                  <#if realm.resetPasswordAllowed>
                    <span><a href="${url.loginResetCredentialsUrl}">${msg("doForgotPassword")}</a></span>
                  </#if>
                </div>
              </div>
            </#if>
          </div>
        </form>
      </#if>
    <#elseif section = "info" >
      <#if realm.password && social.providers??>
        <div id="kc-social-providers">
          <ul>
            <#list social.providers as p>
              <li><a href="${p.loginUrl}" id="zocial-${p.alias}" class="zocial ${p.providerId}"> <span
                          class="text">${p.displayName}</span></a></li>
            </#list>
          </ul>
        </div>
      </#if>
    </#if>
  </div>
</@layout.registrationLayout>
