#deploy.pp
class minid_notification_server::deploy inherits minid_notification_server {

  difilib::spring_boot_deploy { $minid_notification_server::application:
    package      => $minid_notification_server::group_id,
    artifact     => $minid_notification_server::artifact_id,
    service_name => $minid_notification_server::service_name,
    install_dir  => "${minid_notification_server::install_dir}${minid_notification_server::application}",
    artifact_type => "jar",
  }
}
