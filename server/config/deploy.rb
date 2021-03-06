require 'bundler/capistrano'

set :rails_env, "production"
set :application, "fastmap"
set :use_sudo, true
set :user, "deploy"
set :rake, 'bundle exec rake'
set :deploy_subdir, "server"
on :start do
  `ssh-add`
end

set :deploy_via, :remote_cache

set :ssh_options, {:forward_agent => true}
set :domain, "cleverua.net"
set :deploy_to, "/home/#{user}/applications/#{application}"
set :scm, "git"
set :branch, "master"
set :repository,  "git@github.com:cleverua/fastmap.git"
set :keep_releases, 2
default_run_options[:pty] = true
ssh_options[:port] = 22

role :app, domain
role :web, domain
role :db,  domain, :primary => true


after("deploy:finalize_update") do
  ["database"].each do |f|
    run "cp #{current_release}/config/#{f}.example.yml #{current_release}/config/#{f}.yml"
  end
end

after "deploy:restart", :roles => :app do
  find_and_execute_task("deploy:cleanup")
  # find_and_execute_task("restart_stalker_runit")
end


namespace :deploy do
  task :restart do
    begin
      run "#{sudo} sv restart fastmap"
    rescue => e
      puts "======= Unicorn error ========"
      puts e.message
      puts e.backtrace
    end
  end
  task :start do
    begin
      run "#{sudo} sv up fastmap"
    rescue => e
      puts "======= Unicorn error ========"
      puts e.message
      puts e.backtrace
    end
  end
  task :stop do
    begin
      run "#{sudo} sv down fastmap"
    rescue => e
      puts "======= Unicorn error ========"
      puts e.message
      puts e.backtrace
    end
  end
end