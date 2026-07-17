#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
project_dir="$(cd "${script_dir}/.." && pwd)"
build_dir="${project_dir}/build"
classes_dir="${build_dir}/classes"
test_classes_dir="${build_dir}/test-classes"
output_jar="${project_dir}/42/media/java/cjsStealthOverhaul.jar"
ecj_jar="${ECJ_JAR:-${project_dir}/.tools/ecj.jar}"

pz_jar="${PZ_JAR:-/home/cjstorrs/games/Project Zomboid Linux 42.19.0/game/projectzomboid/projectzomboid.jar}"
zombie_buddy_jar="${ZOMBIE_BUDDY_JAR:-/home/cjstorrs/Zomboid/mods/ZombieBuddy/libs/ZombieBuddy.jar}"

if [[ ! -f "${pz_jar}" ]]; then
    echo "Missing Project Zomboid JAR: ${pz_jar}" >&2
    exit 1
fi
if [[ ! -f "${zombie_buddy_jar}" ]]; then
    echo "Missing ZombieBuddy JAR: ${zombie_buddy_jar}" >&2
    exit 1
fi
if [[ ! -f "${ecj_jar}" ]]; then
    echo "Missing Eclipse compiler: ${ecj_jar}" >&2
    echo "B42.19 classes require a Java 25-aware compiler. Set ECJ_JAR or place ecj.jar in .tools/." >&2
    exit 1
fi

mkdir -p "${classes_dir}" "${test_classes_dir}" "$(dirname "${output_jar}")"
find "${classes_dir}" "${test_classes_dir}" -type f -delete

mapfile -t main_sources < <(find "${project_dir}/java/src/main/java" -name '*.java' -type f | sort)
java -jar "${ecj_jar}" -21 -cp "${pz_jar}:${zombie_buddy_jar}" -d "${classes_dir}" "${main_sources[@]}"

mapfile -t test_sources < <(find "${project_dir}/java/src/test/java" -name '*.java' -type f | sort)
java -jar "${ecj_jar}" -21 -cp "${classes_dir}" -d "${test_classes_dir}" "${test_sources[@]}"
java -cp "${classes_dir}:${test_classes_dir}" com.cjstorrs.cjsstealthoverhaul.StealthMathTest

jar --create --file "${output_jar}" -C "${classes_dir}" .
echo "Built ${output_jar}"
