#params.pp
class minid_notification_server::params {
  $java_home                        = hiera('platform::java_home')
  $log_root                         = '/var/log/'
  $log_level                        = 'WARN'
  $install_dir                      = '/opt/'
  $config_dir                       = '/etc/opt/'
  $group_id                         = 'no.idporten'
  $artifact_id                      = 'minid-notification-server'
  $service_name                     = 'minid-notification-server'
  $server_port                      = 8080
  $application                      = 'minid-notification-server'
  $server_tomcat_max_threads        = 200
  $server_tomcat_min_spare_threads  = 10
  $health_show_details              = 'always'
  $auditlog_dir                     = '/var/log/minid-notification-server/audit'
  $auditlog_file                    = 'audit.log'
  $proxy_enabled                    = false
  $proxy_host                       = localhost
  $proxy_port                       = 8081
  $tomcat_tmp_dir                   = '/opt/minid-notification-server/tmp'
  $eventlog_jms_queuename           = hiera('idporten_logwriter::jms_queueName')
  $eventlog_jms_url                 = hiera('platform::jms_url')
  $idporten_oidc_issuer_uri         = ''
  $database_url                     = ''
  $database_username                = ''
  $database_password                = ''
  $swagger_client_id                = ''
  $swagger_client_secret            = ''
  $tokeninfo_client_id              = ''
  $tokeninfo_client_secret          = ''

}
