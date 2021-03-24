#params.pp
class minid_notification_server::params {
  $java_home                        = hiera('platform::java_home')
  $log_root                         = '/var/log/'
  $log_level                        = 'WARN'
  $install_dir                      = '/opt/'
  $config_dir                       = '/etc/opt/'
  $group_id                         = 'no.digdir'
  $artifact_id                      = 'minid-notification-server'
  $service_name                     = 'minid-notification-server'
  $server_port                      = 8080
  $application                      = 'minid-notification-server'
  $server_tomcat_max_threads        = 200
  $server_tomcat_min_spare_threads  = 10
  $health_show_details              = 'always'
  $auditlog_dir                     = '/var/log/minid-notification-server/audit'
  $auditlog_file                    = 'audit-log.json'
  $proxy_enabled                    = false
  $proxy_host                       = localhost
  $proxy_port                       = 8081
  $proxy_skip_hosts                 = 'oidc-test1.difi.eon.no|localhost'
  $tomcat_tmp_dir                   = '/opt/minid-notification-server/tmp'
  $eventlog_jms_queuename           = hiera('idporten_logwriter::jms_queueName')
  $eventlog_jms_url                 = hiera('platform::jms_url')
  $idporten_oidc_issuer_uri         = ''
  $database_url                     = ''
  $database_username                = ''
  $database_password                = ''
  $db_leakDetectionThresholdSeconds = 30
  $db_maxLifetimeMilliSeconds       = 540000
  $db_max_active                    = 10
  $db_pool_manager                  = 'com.zaxxer.hikari.HikariDataSource'  # org.apache.tomcat.jdbc.pool.DataSource for Tomcat pool manager
  $swagger_client_id                = ''
  $swagger_client_secret            = ''
  $tokeninfo_client_id              = ''
  $tokeninfo_client_secret          = ''
  $apns_sandbox                     = true
  $google_api_key                   = ''
  $bundle_id                        = 'no.digdir.minid.authenticator'
  $context_path                     = '/minid-notification-server'
  $cache_local_ttl_in_s             = 5
  $cache_cluster_ttl_in_s           = 300
  $par_cache_ttl_in_s               = 120
  $cache_transport_file_location    = '/etc/opt/minid-notification-server/cache-transport.xml'
  $cache_groups_udp_mcast_port      = 45590
  $cache_groups_udp_bind_addr       = 'match-interface:eth0' # only works if all nodes on same machine. See http://www.jgroups.org/manual/index.html#Transport.
  $infinispan_enabled               = true
  $minid_eid_url                    = 'https://eid-systest-web01.dmz.local/minid-authentication-web'
  $minid_eid_ttl                    = 180

}
