#!/usr/bin/env bash

#DESC:
# script to build docker image and push it to docker hub (assuming user is logged in)

# This file sources in the bulk of the functions from the ../source.sh file

# A better class of script...
set -o errexit          # Exit on most errors (see the manual)
set -o errtrace         # Make sure any error trap is inherited
set -o nounset          # Disallow expansion of unset variables
set -o pipefail         # Use last non-zero exit code in a pipeline
#set -o xtrace          # Trace the execution of the script (debug)

# DESC: Usage help
# ARGS: None
# OUTS: None
function script_usage() {
    cat << EOF
Usage:
     ./build [OPTIONS] <projects, .. >
     Options:
     -h|--help                  Displays this help
     -v|--verbose               Displays verbose output
    -nc|--no-colour             Disables colour output
    -cr|--cron                  Run silently unless we encounter an error
    --push                      push the image to docker flag
    --all-projects              execute for all projects no need to mention all

EOF
}

projects=""
all_projects="config-server discovery entries food-catalog identity"
# DESC: Parameter parser
# ARGS: $@ (optional): Arguments provided to the script
# OUTS: Variables indicating command-line parameters and options
function parse_params() {
    local param
    local all
    all=false
    while [[ $# -gt 0 ]]; do
        param="$1"
        shift
        case $param in
            -h|--help)
                script_usage
                exit 0
                ;;
            --all-projects)
                all=true
                ;;
            -v|--verbose)
                verbose=true
                ;;
            -nc|--no-colour)
                no_colour=true
                ;;
            -cr|--cron)
                cron=true
                ;;
            --push)
                push=true
                ;;
             *)
                break
                ;;
        esac
    done

    projects="$@"
    if [[ -n ${all-} ]]; then
        projects=$all_projects
    fi

}

function build_img() {
    local dir
    local pushArg=""
    dir="$1"
    if [[ -n ${push-} ]]; then
        pushArg="dockerfile:push"
    fi
    cd ../../$dir
    ./mvnw -Dmaven.test.skip=true clean package dockerfile:build $pushArg
    cd $orig_cwd
}

# DESC: Main control flow
# ARGS: $@ (optional): Arguments provided to the script
# OUTS: None
function main() {
    # shellcheck source=source.sh
    source "../$(dirname "${BASH_SOURCE[0]}")/source.sh"

    trap script_trap_err ERR
    trap script_trap_exit EXIT

    script_init "$@"
    parse_params "$@"
    cron_init
    colour_init
    #lock_init system

    docker image prune -f

    for param in $projects; do
        build_img $param
    done

    docker image prune -f
}

# Make it rain
main "$@"

# vim: syntax=sh cc=80 tw=79 ts=4 sw=4 sts=4 et sr


