{{/*
Expand the name of the chart.
*/}}
{{- define "collaborate-dapp.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "collaborate-dapp.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "collaborate-dapp.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "collaborate-dapp.labels" -}}
helm.sh/chart: {{ include "collaborate-dapp.chart" . }}
{{ include "collaborate-dapp.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "collaborate-dapp.selectorLabels" -}}
app.kubernetes.io/name: {{ include "collaborate-dapp.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "collaborate-dapp.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "collaborate-dapp.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Create secret data with automatic initialization
Parameter: [$, Secret name, key, length, user value path]
*/}}
{{- define "collaborate-dapp.automaticSecret" -}}
{{- $ := index . 0 -}}
{{- $name := index . 1 -}}
{{- $key := index . 2 -}}
{{- $secretLength := int (index . 3) }}
{{- $userValue := index . 4 }}
{{- if $userValue }}
  {{ $key }}: {{ $userValue | b64enc | quote }}
{{- else if ($.Values.global).dev }}
  {{ $key }}: {{ printf "%s-%s" $name $key | sha256sum | trunc $secretLength | b64enc | quote }}
{{- else if and ($.Release.IsUpgrade) (lookup "v1" "Secret" $.Release.Namespace $name) }}
  {{ $key }}: {{ index (lookup "v1" "Secret" $.Release.Namespace $name).data $key }}
{{- else }}
  {{ $key }}: {{ randAlphaNum $secretLength | b64enc | quote }}
{{- end }}
{{- end }}

{{/*
Db fullname
*/}}
{{- define "collaborate-dapp.db.fullname" -}}
{{- if .Values.db.fullnameOverride }}
{{- .Values.db.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.db.nameOverride }}
{{- if eq $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Keycloak fullname
*/}}
{{- define "collaborate-dapp.keycloak.fullname" -}}
{{- if .Values.keycloak.fullnameOverride }}
{{- .Values.keycloak.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.keycloak.nameOverride }}
{{- if eq $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}
