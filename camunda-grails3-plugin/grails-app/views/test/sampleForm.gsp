<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>camunda BPM 'embedded' Sample Task Form</title>
</head>
<body>
  <form class="form-horizontal" role="form">
    <div class="panel panel-info">camunda BPM 'embedded' Sample Task Form</div>
    <div class="control-group">
      <label class="control-label">sampleStringField</label>
      <div class="controls">
        <input cam-variable-name="sampleStringField" cam-variable-type="String" type="text" name="sampleStringField" class="form-control" />
      </div>
    </div>
    <div class="control-group">
      <label class="control-label">sampleNumberField</label>
      <div class="controls">
        <input cam-variable-name="sampleNumberField" cam-variable-type="Integer" type="number" name="sampleNumberField" class="form-control" />
      </div>
    </div>
    <div class="control-group">
      <label class="control-label">sampleBooleanField</label>
      <div class="controls">
        <input cam-variable-name="sampleBooleanField" cam-variable-type="Boolean" type="checkbox" name="sampleBooleanField" class="form-control" />
      </div>
    </div>
    <div class="control-group">
      <label class="control-label">sampleDateField</label>
      <div class="controls">
        <input cam-variable-name="sampleDateField" cam-variable-type="Date" type="date" name="sampleDateField" class="form-control" />
      </div>
    </div>
    <div class="control-group">
      <label class="control-label">sampleRequiredSelectionField</label>
      <div class="controls">
        <select cam-variable-name="sampleRequiredSelectionField" cam-variable-type="String" type="text" name="sampleRequiredSelectionField" class="form-control" required>
          <option></option>
          <option value="john">John</option>
          <option value="peter">Peter</option>
          <option value="mary">Mary</option>
        </select>
      </div>
    </div>
    <div class="control-group">
      <label class="control-label">sampleReadonlyField</label>
      <div class="controls">
        <input cam-variable-name="sampleReadonlyField" type="text" name="sampleReadonlyField" readonly="true" class="form-control" />
      </div>
    </div>
  </form>
</body>
</html>