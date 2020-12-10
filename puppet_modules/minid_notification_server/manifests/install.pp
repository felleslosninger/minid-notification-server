#install.pp
class minid_notification_server::install inherits minid_notification_server {

  user { $minid_notification_server::service_name:
    ensure => present,
    shell  => '/sbin/nologin',
    home   => '/',
  } ->
  file { "${minid_notification_server::config_dir}${minid_notification_server::application}":
    ensure => 'directory',
    mode   => '0755',
    owner  => $minid_notification_server::service_name,
    group  => $minid_notification_server::service_name,
  } ->
  file { "${minid_notification_server::config_dir}${minid_notification_server::application}/config":
    ensure => 'directory',
    owner  => $minid_notification_server::service_name,
    group  => $minid_notification_server::service_name,
    mode   => '0755',
  } ->
  file { "${minid_notification_server::log_root}${minid_notification_server::application}":
    ensure => 'directory',
    mode   => '0755',
    owner  =>  $minid_notification_server::service_name,
    group  =>  $minid_notification_server::service_name,
  } ->
  file { "${minid_notification_server::install_dir}${minid_notification_server::application}":
    ensure => 'directory',
    mode   => '0644',
    owner  =>  $minid_notification_server::service_name,
    group  =>  $minid_notification_server::service_name,
  }

  difilib::spring_boot_logrotate { $minid_notification_server::application:
    application => $minid_notification_server::application,
  }

  if ($platform::install_cron_jobs) {
    $log_cleanup_command = "find ${minid_notification_server::log_root}${minid_notification_server::application}/ -type f -name \"*.gz\" -mtime +7 -exec rm -f {} \\;"

    cron { "${minid_notification_server::application}_log_cleanup":
      command => $log_cleanup_command,
      user    => 'root',
      hour    => '03',
      minute  => '00',
    }
  }
}
