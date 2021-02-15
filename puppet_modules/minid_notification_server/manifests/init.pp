class minid_notification_server (
  String $log_root                         = $minid_notification_server::params::log_root,
  String $log_level                        = $minid_notification_server::params::log_level,
  String $install_dir                      = $minid_notification_server::params::install_dir,
  String $config_dir                       = $minid_notification_server::params::config_dir,
  String $group_id                         = $minid_notification_server::params::group_id,
  String $artifact_id                      = $minid_notification_server::params::artifact_id,
  String $service_name                     = $minid_notification_server::params::service_name,
  Integer $server_port                     = $minid_notification_server::params::server_port,
  String $application                      = $minid_notification_server::params::application,
  Integer $server_tomcat_max_threads       = $minid_notification_server::params::server_tomcat_max_threads,
  Integer $server_tomcat_min_spare_threads = $minid_notification_server::params::server_tomcat_min_spare_threads,
  String $health_show_details              = $minid_notification_server::params::health_show_details,
  String $auditlog_dir                     = $minid_notification_server::params::auditlog_dir,
  String $auditlog_file                    = $minid_notification_server::params::auditlog_file,
  Boolean $proxy_enabled                   = $minid_notification_server::params::proxy_enabled,
  String $proxy_host                       = $minid_notification_server::params::proxy_host,
  Integer $proxy_port                      = $minid_notification_server::params::proxy_port,
  String $proxy_skip_hosts                 = $minid_notification_server::params::proxy_skip_hosts,
  String $tomcat_tmp_dir                   = $minid_notification_server::params::tomcat_tmp_dir,
  String $eventlog_jms_queuename           = $minid_notification_server::params::eventlog_jms_queuename,
  String $eventlog_jms_url                 = $minid_notification_server::params::eventlog_jms_url,
  String $idporten_oidc_issuer_uri         = $minid_notification_server::params::idporten_oidc_issuer_uri,
  String $database_url                     = $minid_notification_server::params::database_url,
  String $database_username                = $minid_notification_server::params::database_username,
  String $database_password                = $minid_notification_server::params::database_password,
  Integer $db_leakDetectionThresholdSeconds = $minid_notification_server::params::db_leakDetectionThresholdSeconds,
  Integer $db_maxLifetimeMilliSeconds      = $minid_notification_server::params::db_maxLifetimeMilliSeconds,
  Integer $db_max_active                   = $minid_notification_server::params::db_max_active,
  String $db_pool_manager                  = $minid_notification_server::params::db_pool_manager,
  String $swagger_client_id                = $minid_notification_server::params::swagger_client_id,
  String $swagger_client_secret            = $minid_notification_server::params::swagger_client_secret,
  String $tokeninfo_client_id              = $minid_notification_server::params::tokeninfo_client_id,
  String $tokeninfo_client_secret          = $minid_notification_server::params::tokeninfo_client_secret,
  Boolean $apns_sandbox                    = $minid_notification_server::params::apns_sandbox,
  String $google_api_key                   = $minid_notification_server::params::google_api_key,
  String $bundle_id                        = $minid_notification_server::params::bundle_id,
  String $context_path                     = $minid_notification_server::params::context_path

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
