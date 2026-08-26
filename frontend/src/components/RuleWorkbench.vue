<!-- Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ -->
<script setup>
import { reactive, ref } from 'vue'
import { request } from '../api'

const fields = [{"key":"appointmentNo","label":"预约编号","type":"text"},{"key":"dockNo","label":"月台编号","type":"text"},{"key":"arrival","label":"到达时间","type":"datetime-local"},{"key":"windowStart","label":"时窗开始","type":"datetime-local"},{"key":"windowEnd","label":"时窗结束","type":"datetime-local"},{"key":"dockAvailable","label":"月台可用","type":"boolean"},{"key":"vehicleCertified","label":"车辆资质有效","type":"boolean"},{"key":"loadUnits","label":"作业量","type":"number"},{"key":"dockCapacity","label":"时段能力","type":"number"}]
const form = reactive({"appointmentNo":"APT-001","dockNo":"D-01","arrival":"2026-08-26T09:10","windowStart":"2026-08-26T09:00","windowEnd":"2026-08-26T10:00","dockAvailable":true,"vehicleCertified":true,"loadUnits":20,"dockCapacity":30})
const result = ref(null)
const error = ref('')
const busy = ref(false)

function payload() {
  const data = {}
  for (const field of fields) {
    const value = form[field.key]
    if (field.type === 'number') data[field.key] = Number(value)
    else if (field.type === 'list') data[field.key] = String(value).split(',').map(item => item.trim()).filter(Boolean)
    else if (field.type === 'json') data[field.key] = JSON.parse(value)
    else data[field.key] = value
  }
  return data
}

async function run() {
  busy.value = true
  error.value = ''
  try { result.value = await request('/api/enterprise/yms/evaluate-slot', { method: 'POST', body: JSON.stringify(payload()) }) }
  catch (e) { error.value = e.message }
  finally { busy.value = false }
}
const show = value => Array.isArray(value) ? (value.length ? value.join('；') : '无') : (typeof value === 'object' ? JSON.stringify(value) : String(value))
</script>

<template>
  <section class="rule-head">
    <div><span>DOMAIN CONTROL</span><h3>车辆预约与月台准入</h3><p>校验预约时窗、车辆资质、月台状态与单时段作业能力。</p></div>
    <b>服务端强校验</b>
  </section>
  <section class="rule-layout">
    <form class="rule-form" @submit.prevent="run">
      <label v-for="field in fields" :key="field.key" :class="{wide: field.type === 'json' || field.type === 'list'}">
        <span>{{ field.label }}</span>
        <input v-if="field.type !== 'boolean' && field.type !== 'json'" v-model="form[field.key]" :type="field.type === 'list' ? 'text' : field.type" :step="field.type === 'number' ? 'any' : undefined" required>
        <textarea v-else-if="field.type === 'json'" v-model="form[field.key]" rows="5" required></textarea>
        <span v-else class="switch"><input v-model="form[field.key]" type="checkbox"><i></i>{{ form[field.key] ? '是' : '否' }}</span>
      </label>
      <button class="run" :disabled="busy">{{ busy ? '正在校验…' : '执行规则校验' }}</button>
      <p v-if="error" class="rule-error">{{ error }}</p>
    </form>
    <aside class="result">
      <template v-if="result">
        <span>DECISION</span><h3>{{ result.decision || '已完成' }}</h3>
        <dl><template v-for="(value,key) in result" :key="key"><dt>{{key}}</dt><dd>{{show(value)}}</dd></template></dl>
      </template>
      <div v-else class="empty"><b>等待试算</b><p>填写业务数据后，服务端将返回可审计的计算指标、阻断原因和处理结论。</p></div>
    </aside>
  </section>
</template>

<style scoped>
.rule-head{display:flex;align-items:end;justify-content:space-between;padding:26px 30px;margin-bottom:18px;background:#fff;border:1px solid #e2e8eb;border-radius:14px}.rule-head span,.result>span{font-size:12px;letter-spacing:.16em;color:#61727b}.rule-head h3{font-size:24px;margin:7px 0}.rule-head p{margin:0;color:#667780}.rule-head>b{padding:8px 12px;border-radius:20px;background:#e8f3ee;color:#237052;font-size:13px}.rule-layout{display:grid;grid-template-columns:minmax(0,1.25fr) minmax(320px,.75fr);gap:18px}.rule-form,.result{background:#fff;border:1px solid #e2e8eb;border-radius:14px;padding:28px}.rule-form{display:grid;grid-template-columns:1fr 1fr;gap:18px}.rule-form label{display:flex;flex-direction:column;gap:8px;font-size:13px;color:#44545d}.rule-form label.wide,.run,.rule-error{grid-column:1/-1}.rule-form input,.rule-form textarea{box-sizing:border-box;width:100%;border:1px solid #ccd6db;border-radius:8px;padding:11px 12px;font:inherit;background:#fbfcfd}.switch{display:flex;align-items:center;gap:9px;height:42px}.switch input{width:18px;height:18px}.run{border:0;border-radius:8px;padding:13px;background:#234f66;color:#fff;font-weight:700;cursor:pointer}.result h3{font-size:25px;margin:8px 0 22px}.result dl{margin:0}.result dt{font-size:12px;color:#78868e;margin-top:14px}.result dd{margin:4px 0;padding-bottom:10px;border-bottom:1px solid #edf0f2;overflow-wrap:anywhere}.empty{display:grid;place-content:center;min-height:260px;color:#75838a}.empty b{font-size:22px;color:#44545d}.empty p{max-width:300px;line-height:1.7}.rule-error{color:#a53535;margin:0}@media(max-width:900px){.rule-layout{grid-template-columns:1fr}.rule-form{grid-template-columns:1fr}.rule-form label.wide,.run,.rule-error{grid-column:auto}}
</style>

