#service.pp
class minid_notification_server::service inherits minid_notification_server {

  include platform

  if ($platform::deploy_spring_boot) {
    service { $minid_notification_server::service_name:
      ensure => running,
      enable => true,
    }
  }
}
