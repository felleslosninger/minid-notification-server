#config.pp
class minid_notification_server::config inherits minid_notification_server {

  file { "${minid_notification_server::install_dir}${minid_notification_server::application}/${minid_notification_server::artifact_id}.conf":
    ensure  => 'file',
    content => template("${module_name}/${minid_notification_server::artifact_id}.conf.erb"),
    owner   => $minid_notification_server::service_name,
    group   => $minid_notification_server::service_name,
    mode    => '0444',
  } ->
  file { "${minid_notification_server::config_dir}${minid_notification_server::application}/application.yml":
    ensure  => 'file',
    content => template("${module_name}/application.yml.erb"),
    owner   => $minid_notification_server::service_name,
    group   => $minid_notification_server::service_name,
    mode    => '0444',
  } ->
  file { "${minid_notification_server::config_dir}${minid_notification_server::application}/cache-transport.xml":
          ensure  => 'file',
          content => template("${module_name}/cache-transport.xml.erb"),
          owner   => $minid_notification_server::service_name,
          group   => $minid_notification_server::service_name,
          mode    => '0644',
    } ->
  file { "/etc/rc.d/init.d/${minid_notification_server::service_name}":
    ensure => 'link',
    target => "${minid_notification_server::install_dir}${minid_notification_server::application}/${minid_notification_server::artifact_id}.jar",
  }

  difilib::logback_config { $minid_notification_server::application:
    application       => $minid_notification_server::application,
    owner             => $minid_notification_server::service_name,
    group             => $minid_notification_server::service_name,
    loglevel_no       => $minid_notification_server::log_level,
    loglevel_nondifi  => $minid_notification_server::log_level,
  }


}
