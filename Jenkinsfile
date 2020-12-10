pipelineWithMavenAndDocker {
    enableDependencyTrack = true
    verificationEnvironment = 'eid-verification2'
    stagingEnvironment = 'eid-staging'
    stagingEnvironmentType = 'puppet2'
    productionEnvironment = 'eid-production'
    gitSshKey = 'ssh.github.com'
    puppetModules = 'minid-notification-server'
    librarianModules = 'DIFI-minid_notification_server'
    puppetApplyList = ['eid-systest-app01 baseconfig,minid_notification_server']
}
