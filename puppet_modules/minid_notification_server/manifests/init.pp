class minid_notification_server (
  String $log_root                         = $minid_notification_server::params::log_root,
  String $log_level                        = $minid_notification_server::params::log_level,
  String $install_dir                      = $minid_notification_server::params::install_dir,
  String $config_dir                       = $minid_notification_server::params::config_dir,
  String $group_id                         = $minid_notification_server::params::group_id,
  String $artifact_id                      = $minid_notification_server::params::artifact_id,
  String $service_name                     = $minid_notification_server::params::service_name,
  String $server_port                      = $minid_notification_server::params::server_port,
  String $application                      = $minid_notification_server::params::application,
  Integer $server_tomcat_max_threads       = $minid_notification_server::params::server_tomcat_max_threads,
  Integer $server_tomcat_min_spare_threads = $minid_notification_server::params::server_tomcat_min_spare_threads,
  String $health_show_details              = $minid_notification_server::params::health_show_details,
  String $auditlog_dir                     = $minid_notification_server::params::auditlog_dir,
  String $auditlog_file                    = $minid_notification_server::params::auditlog_file,
  String $tomcat_tmp_dir                   = $minid_notification_server::params::tomcat_tmp_dir,
  Integer $token_lifetime_seconds          = $minid_notification_server::params::token_lifetime_seconds,
  Integer $code_lifetime_seconds           = $minid_notification_server::params::code_lifetime_seconds,
  String $eventlog_jms_queuename           = $minid_notification_server::params::eventlog_jms_queuename,
  String $eventlog_jms_url                 = $minid_notification_server::params::eventlog_jms_url,
  Integer $session_ttl_seconds             = $minid_notification_server::params::session_ttl_seconds,
  String $idporten_oidc_issuer_uri         = $minid_notification_server::params::idporten_oidc_issuer_uri,
  String $database_url                     = $minid_notification_server::params::database_url,
  String $database_username                = $minid_notification_server::params::database_username,
  String $database_password                = $minid_notification_server::params::database_password,
  String $swagger_client_id                = $minid_notification_server::params::swagger_client_id,
  String $swagger_client_secret            = $minid_notification_server::params::swagger_client_secret,
  String $tokeninfo_client_id              = $minid_notification_server::params::tokeninfo_client_id,
  String $tokeninfo_client_secret          = $minid_notification_server::params::tokeninfo_client_secret

) inherits minid_notification_server::params {

  include platform

  anchor { 'minid_notification_server::begin': } ->
  class { '::minid_notification_server::install': } ->
  class { '::minid_notification_server::test_setup': } ->
  class { '::minid_notification_server::deploy': } ->
  class { '::minid_notification_server::config': } ~>
  class { '::minid_notification_server::service': } ->
  anchor { 'minid_notification_server::end': }

}
